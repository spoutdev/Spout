/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.spout.api.Spout;

public class SecurityHandler {

	private static final ConcurrentHashMap<String, KeyPair> serverKeys = new ConcurrentHashMap<String, KeyPair>();
	private static final Provider provider;
	private static final SecurityHandler instance;
	
	static {
		Provider p = Security.getProvider("BC");
		if (p == null) {
			Security.addProvider(new BouncyCastleProvider());
			p = Security.getProvider("BC");
			if (p == null) {
				Spout.getLogger().info("Unable to start security provider");
			}
		}
		provider = p;
		instance = new SecurityHandler();
	}
	
	public static SecurityHandler getInstance() {
		return instance;
	}
	
	public Provider getProvider() {
		return provider;
	}

	public KeyPair getKeyPair(String algorithm) {
		return getKeyPair(1024, algorithm);
	}

	public KeyPair getKeyPair(int keySize, String algorithm) {
		return getKeyPair(keySize, algorithm, "SHA1PRNG", "SUN");
	}

	public KeyPair getKeyPair(int keySize, String algorithm, String RNGAlgorithm, String RNGProvider) {
		KeyPair pair = serverKeys.get(algorithm);
		if (pair == null) {
			SecureRandom secureRandom;
			try {
				secureRandom = SecureRandom.getInstance(RNGAlgorithm, RNGProvider);
				secureRandom.nextBytes(new byte[1]);
			} catch (NoSuchProviderException e) {
				Spout.getLogger().info("Unable to find algorithm to for random number generator");
				return null;
			} catch (NoSuchAlgorithmException e) {
				Spout.getLogger().info("Unable to find algorithm to generate random number generator for key pair creation");
				return null;
			}

			KeyPairGenerator generator;
			try {
				generator = KeyPairGenerator.getInstance(algorithm, provider);
			} catch (NoSuchAlgorithmException e) {
				Spout.getLogger().info("Unable to find algorithm to generate key pair");
				return null;
			}

			generator.initialize(keySize, secureRandom);

			KeyPair newPair = generator.generateKeyPair();

			KeyPair oldPair = serverKeys.putIfAbsent(algorithm, newPair);

			if (oldPair == null) {
				return newPair;
			} else {
				return oldPair;
			}

		} else {
			return pair;
		}

	}
}
