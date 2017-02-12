package org.gotti.wurmunlimited.clusterconfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClusterNode {

	private final NodeType nodeType;

	private final String name;

	private final int id;

	private final String password;

	private final String publicIp;

	private final int publicPort;
	
	private final String internalIp;

	private final int internalPort;
	
	private final int rmiPort;

	private final int registrationPort;

	private final boolean rmiEnabled;

	private final Map<Direction, String> directions;

	private final Map<String, String> options;

	public ClusterNode(String name, ConfigHelper helper) {
		this.name = Objects.requireNonNull(name, "name");
		this.nodeType = NodeType.parse(helper.getProperty("type"));
		this.id = Integer.parseInt(helper.getProperty("id"));

		ConfigHelper pub = helper.getNode("public");
		this.publicIp = pub.getProperty("ip");
		this.publicPort = Integer.parseInt(pub.getProperty("port"));
		
		ConfigHelper intern = helper.getNode("internal");
		this.internalIp = intern.getProperty("ip");
		this.internalPort = Integer.parseInt(intern.getProperty("port"));
		this.rmiPort = Integer.parseInt(intern.getProperty("rmi_port"));
		this.registrationPort = Integer.parseInt(intern.getProperty("registration_port"));
		this.password = intern.getProperty("password");

		this.rmiEnabled = Boolean.parseBoolean(helper.getProperty("rmi", "true"));

		this.directions = new HashMap<>();

		ConfigHelper dir = helper.getNode("directions", Collections.emptyMap());
		for (String key : dir.getKeys()) {
			Direction d = Direction.valueOf(key.toUpperCase(Locale.ROOT));
			this.directions.put(d, dir.getProperty(key));
		}

		this.options = new LinkedHashMap<String, String>();
		ConfigHelper opt = helper.getNode("options", Collections.emptyMap());
		for (String key : opt.getKeys()) {
			options.put(key, opt.getProperty(key));
		}
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public int getPublicPort() {
		return publicPort;
	}
	
	public String getInternalIp() {
		return internalIp;
	}
	
	public int getInternalPort() {
		return internalPort;
	}

	public int getRmiPort() {
		return rmiPort;
	}

	public int getRegistrationPort() {
		return registrationPort;
	}

	public boolean isRmiEnabled() {
		return rmiEnabled;
	}

	public Map<Direction, String> getDirections() {
		return directions;
	}

	public Map<String, String> getOptions() {
		return options;
	}
}
