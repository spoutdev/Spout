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
package org.spout.api.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.spout.api.Spout;

public class SecurityHandler {
	
	public static final boolean DECRYPT_MODE = false;
	public static final boolean ENCRYPT_MODE = false;
	

	private static final ConcurrentHashMap<String, AsymmetricCipherKeyPair > serverKeys = new ConcurrentHashMap<String, AsymmetricCipherKeyPair >();
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
	
	public byte[] encodeKey(CipherParameters key) {
		if (!(key instanceof RSAKeyParameters)) {
			return null;
		}

		if (((RSAKeyParameters) key).isPrivate()) {
			return null;
		}

		RSAKeyParameters rsaKey = (RSAKeyParameters) key;

		ASN1EncodableVector encodable = new ASN1EncodableVector();
		encodable.add(new ASN1Integer(rsaKey.getModulus()));
		encodable.add(new ASN1Integer(rsaKey.getExponent()));

		return KeyUtil.getEncodedSubjectPublicKeyInfo(
				new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()), 
				new DERSequence(encodable));
	}
	
	public BufferedBlockCipher getSymmetricCipher(String cipher, String wrapper) {
		if (cipher.equals("AES")) {
			return addSymmetricWrapper(new AESEngine(), wrapper);
		}

		return null;
	}
	
	private BufferedBlockCipher addSymmetricWrapper(BlockCipher rawCipher, String wrapper) {
		if (wrapper.startsWith("CFB")) {
			int bits;
			try {
				bits = Integer.parseInt(wrapper.substring(3));
			} catch (NumberFormatException e) {
				Spout.getLogger().info("Unable to parse bits for CFB wrapper from: " + wrapper);
				return null;
			}
			return new BufferedBlockCipher(new CFBBlockCipher(rawCipher, bits));
		}

		return new BufferedBlockCipher(rawCipher);
	}
	
	public PaddedBufferedBlockCipher addSymmetricPadding(BlockCipher rawCipher, String padding) {
		if (padding.equals("PKCS7")) {
			return new PaddedBufferedBlockCipher(rawCipher);
		}

		return null;
	}
	
	public AsymmetricBlockCipher getAsymmetricCipher(String cipher, String padding) {
		if (cipher.equals("RSA")) {
			return addAsymmetricPadding(new RSAEngine(), padding);
		}

		return null;
	}
	
	private AsymmetricBlockCipher addAsymmetricPadding(AsymmetricBlockCipher rawCipher, String padding) {
		if (padding == null) {
			return rawCipher;
		} else if (padding.equals("PKCS1")) {
			return new PKCS1Encoding(rawCipher);
		} else {
			return null;
		}
	}
	
	public AsymmetricCipherKeyPairGenerator getGenerator(String algorithm) {
		if (algorithm.equals("RSA")) {
			return new RSAKeyPairGenerator();
		}

		Spout.getLogger().info("Unable to find key generator " + algorithm);
		return null;
	}

	public void initGenerator(int keySize, String algorithm, AsymmetricCipherKeyPairGenerator generator, SecureRandom random) {
		if (algorithm.equals("RSA")) {
			RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(
					new BigInteger("10001", 16), random, keySize, 80);
			generator.init(params);
		}
	}

	public byte[] processAll(AsymmetricBlockCipher cipher, byte[] input) {
		int outputSize = 0;
		int blockSize = cipher.getInputBlockSize();
		List<byte[]> outputBlocks = new LinkedList<byte[]>();

		int pos = 0;

		while (pos < input.length) {
			int length = Math.min(input.length - pos, blockSize);
			byte[] result;
			try {
				result = cipher.processBlock(input, pos, length);
			} catch (InvalidCipherTextException e) {
				Spout.getLogger().info("Error processing encrypted data");
				return null;
			}
			outputSize += result.length;
			outputBlocks.add(result);
			pos += length;
		}

		byte[] output = new byte[outputSize];

		pos = 0;
		for (byte[] block : outputBlocks) {
			System.arraycopy(block, 0, output, pos, block.length);
			pos += block.length;
		}

		return output;
	}

	public AsymmetricCipherKeyPair  getKeyPair(String algorithm) {
		return getKeyPair(1024, algorithm);
	}

	public AsymmetricCipherKeyPair  getKeyPair(int keySize, String algorithm) {
		return getKeyPair(keySize, algorithm, "SHA1PRNG", "SUN");
	}

	public AsymmetricCipherKeyPair  getKeyPair(int keySize, String algorithm, String RNGAlgorithm, String RNGProvider) {
		AsymmetricCipherKeyPair pair = serverKeys.get(algorithm);
		if (pair != null) {
			return pair;
		}

		if (provider == null) {
			return pair;
		}

		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance(RNGAlgorithm, RNGProvider);
		} catch (NoSuchProviderException e) {
			Spout.getLogger().info("Unable to find algorithm to for random number generator");
			return null;
		} catch (NoSuchAlgorithmException e) {
			Spout.getLogger().info("Unable to find algorithm to generate random number generator for key pair creation");
			return null;
		}

		AsymmetricCipherKeyPairGenerator generator = getGenerator(algorithm);

		if (generator == null) {
			return null;
		}

		initGenerator(keySize, algorithm, generator, secureRandom);

		AsymmetricCipherKeyPair newPair = generator.generateKeyPair();

		AsymmetricCipherKeyPair oldPair = serverKeys.putIfAbsent(algorithm, newPair);
		if (oldPair != null) {
			return oldPair;
		}

		return newPair;
	}
}
