/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.protocol;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.spout.api.Spout;
import org.spout.api.protocol.Message;
import org.spout.engine.SpoutConfiguration;

public class NetworkSendThread {

	private final static int QUEUE_ID_MASK = 7;
	
	private final static long minimumLatency = SpoutConfiguration.SEND_LATENCY.getLong();
	private final static long spikeLatency = SpoutConfiguration.SEND_SPIKE_LATENCY.getLong();
	private final static float spikeChance = SpoutConfiguration.SEND_SPIKE_CHANCE.getFloat() / 10.0F;

	private final int poolIndex;

	private final AtomicReference<ChannelQueueThread[]> channelQueues = new  AtomicReference<ChannelQueueThread[]>();

	private final AtomicReference<ChannelQueueThread[]> interruptedQueues = new AtomicReference<ChannelQueueThread[]>();
	
	public NetworkSendThread(int poolIndex) {
		this.poolIndex = poolIndex;
		channelQueues.set(new ChannelQueueThread[16]);
	}

	public void send(SpoutSession<?> session, Channel channel, Message message) {
		ChannelQueueThread queue = getChannelQueue(message.getChannelId());
		if (queue != null) {
			queue.send(session, channel, message);
		}
	}

	private ChannelQueueThread getChannelQueue(int queueId) {
		queueId = queueId & QUEUE_ID_MASK;

		ChannelQueueThread[] queues = channelQueues.get();
		if (queues == null) {
			return null;
		}
		while (queueId >= queues.length || queues[queueId] == null) {
			ChannelQueueThread[] newQueues = new ChannelQueueThread[queueId + 1];
			for (int i = 0; i < queues.length; i++) {
				newQueues[i] = queues[i];
			}
			ChannelQueueThread newQueue = new ChannelQueueThread(poolIndex, queueId);
			newQueues[queueId] = newQueue;
			if (channelQueues.compareAndSet(queues, newQueues)) {
				newQueue.start();
				queues = newQueues;
			}
		}
		return queues[queueId];
	}

	public void interrupt() {
		ChannelQueueThread[] queues = channelQueues.getAndSet(null);
		if (queues == null) {
			return;
		}
		for (int i = 0; i < queues.length; i++) {
			ChannelQueueThread t = queues[i];
			if (t != null) {
				t.interrupt();
			}
		}
		interruptedQueues.set(queues);
	}

	public void interruptAndJoin() throws InterruptedException {
		ChannelQueueThread[] queues = interruptedQueues.get();
		if (queues == null) {
			interrupt();
			queues = interruptedQueues.get();
		}
		for (int i = 0; i < queues.length; i++) {
			ChannelQueueThread t = queues[i];
			if (t != null) {
				t.join();
			}
		}
	}

	private static class ChannelQueueThread extends Thread {
		
		private long nextSpikeCheck = 0L;

		private final LinkedBlockingQueue<QueueNode> queue = new LinkedBlockingQueue<QueueNode>();

		public ChannelQueueThread(int poolIndex, int channelId) {
			super("Channel queue thread, pool index " + poolIndex + " channel id " + channelId);
		}

		public void send(SpoutSession<?> session, Channel channel, Message message) {
			queue.add(new QueueNode(session, channel, message));
		}

		public void run() {
			Random r = new Random();
			QueueNode node;
			while (!isInterrupted()) {
				if (spikeChance > 0) {
					long currentTime = System.currentTimeMillis();
					if (currentTime > nextSpikeCheck) {
						nextSpikeCheck = currentTime + 100L;
						if (r.nextFloat() < spikeChance) {
							try {
								long spike = (long) (spikeLatency * r.nextFloat());
								Thread.sleep(spike);
							} catch (InterruptedException ie) {
								break;
							}
						}
					}
				}
				try {
					node = queue.take();
				} catch (InterruptedException ie) {
					break;
				}
				try {
					if (minimumLatency > 0) {
						long currentTime = System.currentTimeMillis();
						long w = minimumLatency + node.getCreationTime() - currentTime;
						if (w > 0) {
							try {
								Thread.sleep(w);
							} catch (InterruptedException ie) {
								break;
							}
						}
					}
				} finally {
					handle(node);
				}
			}
			flushQueue();
		}

		private void handle(QueueNode node) {
			Channel channel = node.getChannel();
			try {
				if (channel.isOpen()) {
					channel.write(node.getMessage());
				}
			} catch (Exception e) {
				try {
					node.getSession().disconnect(false, new Object[] {"Socket Error!"});
				} catch (Exception e2) {
					try {
						Spout.getLogger().info("Unable to cleanly close session for " + node.getSession().getPlayer().getName());
					} catch (Exception e3) {
						Spout.getLogger().info("Unable to cleanly close session for unknown player (Unable to get player name)");
					}
				}
			}
		}

		private void flushQueue() {
			QueueNode node;
			while ((node = queue.poll()) != null) {
				handle(node);
			}
		}

	}

	private static class QueueNode {
		private final SpoutSession<?> session;
		private final Channel channel;
		private final Message message;
		private final long creation;

		public QueueNode(SpoutSession<?> session, Channel channel, Message message) {
			this.channel = channel;
			this.message = message;
			this.session = session;
			this.creation = System.currentTimeMillis();
		}

		public Channel getChannel() {
			return channel;
		}

		public SpoutSession<?> getSession() {
			return session;
		}

		public Message getMessage() {
			return message;
		}
		
		public long getCreationTime() {
			return creation;
		}

	}

}
