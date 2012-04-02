package org.spout.api.protocol.common;

import org.spout.api.protocol.CodecLookupService;
import org.spout.api.protocol.common.codec.CustomDataCodec;

public class CommonBootstrapCodecLookupService extends CodecLookupService {
	public CommonBootstrapCodecLookupService() {
		super();
		try {
			/* 0xFA */
			bind(CustomDataCodec.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
}
