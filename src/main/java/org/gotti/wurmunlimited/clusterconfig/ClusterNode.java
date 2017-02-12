package org.gotti.wurmunlimited.clusterconfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClusterNode {

	private NodeType nodeType;

	private String name;

	private int id;

	private String password;

	private String publicIp;

	private int gamePort;

	private int rmiPort;

	private int rmiRegistration;

	private boolean rmiEnabled;

	private Map<Direction, String> directions;

	private Map<String, String> options;

	public ClusterNode(String name, ConfigHelper helper) {
		this.name = Objects.requireNonNull(name, "name");
		this.nodeType = NodeType.parse(helper.getProperty("type"));
		this.id = Integer.parseInt(helper.getProperty("id"));
		this.password = helper.getProperty("password");

		ConfigHelper pub = helper.getNode("public");
		this.publicIp = pub.getProperty("ip");
		this.gamePort = Integer.parseInt(pub.getProperty("game_port"));
		this.rmiPort = Integer.parseInt(pub.getProperty("rmi_port"));
		this.rmiRegistration = Integer.parseInt(pub.getProperty("rmi_registration"));

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

	public int getGamePort() {
		return gamePort;
	}

	public int getRmiPort() {
		return rmiPort;
	}

	public int getRmiRegistration() {
		return rmiRegistration;
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
