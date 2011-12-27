package org.getspout.api.protocol.bootstrap;

import org.getspout.api.protocol.CodecLookupService;
import org.getspout.api.protocol.bootstrap.codec.BootstrapHandshakeCodec;
import org.getspout.api.protocol.bootstrap.codec.BootstrapIdentificationCodec;
import org.getspout.api.protocol.bootstrap.codec.BootstrapPingCodec;

public class BootstrapCodecLookupService extends CodecLookupService {
	
	public BootstrapCodecLookupService() {
		
		try {
		/* 0x01 */bind(BootstrapIdentificationCodec.class);
		/* 0x02 */bind(BootstrapHandshakeCodec.class);
		/* 0xFE */bind(BootstrapPingCodec.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

}
