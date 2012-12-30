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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.spout.api.protocol.Message;

public class NetworkSendThread {

	private final static int QUEUE_ID_MASK = 7;

	private final int poolIndex;

	private final AtomicReference<ChannelQueueThread[]> channelQueues = new  AtomicReference<ChannelQueueThread[]>();

	private final AtomicReference<ChannelQueueThread[]> interruptedQueues = new AtomicReference<ChannelQueueThread[]>();

	public NetworkSendThread(int poolIndex) {
		this.poolIndex = poolIndex;
		channelQueues.set(new ChannelQueueThread[0]);
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

		private final LinkedBlockingQueue<QueueNode> queue = new LinkedBlockingQueue<QueueNode>();

		public ChannelQueueThread(int poolIndex, int channelId) {
			super("Channel queue thread, pool index " + poolIndex + " channel id " + channelId);
		}

		public void send(SpoutSession<?> session, Channel channel, Message message) {
			queue.add(new QueueNode(session, channel, message));
		}

		public void run() {
			QueueNode node;
			while (!isInterrupted()) {
				try {
					node = queue.take();
				} catch (InterruptedException ie) {
					break;
				}
				handle(node);
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
				node.getSession().disconnect(false, new Object[] {"Socket Error!"});
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

		public QueueNode(SpoutSession<?> session, Channel channel, Message message) {
			this.channel = channel;
			this.message = message;
			this.session = session;
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

	}

}
