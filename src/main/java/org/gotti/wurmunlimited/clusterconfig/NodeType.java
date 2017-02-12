package org.gotti.wurmunlimited.clusterconfig;

import java.util.Locale;
import java.util.Objects;

public enum NodeType {

	/**
	 * Login server
	 */
	LOGIN,

	/**
	 * Slave to a login server
	 */
	SLAVE {
		@Override
		public boolean isLoginServer() {
			return false;
		}
	},

	/**
	 * Standalone (login + game server)
	 */
	STANDALONE,
	
	TEST {
		@Override
		public boolean isTestServer() {
			return true;
		}
	}
	;
	
	public static NodeType parse(String string) {
		final String uc = Objects.requireNonNull(string, "node type must not be null").toUpperCase(Locale.ROOT);
		return NodeType.valueOf(uc);
	}
	
	public boolean isLoginServer() {
		return true;
	}
	
	public boolean isTestServer() {
		return false;
	}
}
