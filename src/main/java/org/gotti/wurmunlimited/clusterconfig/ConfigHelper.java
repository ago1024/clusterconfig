package org.gotti.wurmunlimited.clusterconfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfigHelper {

	private Map<String, Object> config;

	public ConfigHelper(Map<String, Object> config) {
		this.config = Objects.requireNonNull(config, "config must not be null");
	}
	
	public String getProperty(String name, String def) {
		if (config.get(name) == null) {
			return def;
		} else {
			return getProperty(name);
		}
	}
	
	public String getProperty(String name) {
		Object object = Objects.requireNonNull(config.get(name), name);
		if (object instanceof List || object instanceof Map) {
			throw new IllegalArgumentException("Property expected: " + name);
		}
		return object.toString();
	}
	
	public List<?> getList(String name, List<?> def) {
		if (config.get(name) == null) {
			return def;
		} else {
			return getList(name);
		}
	}

	public List<?> getList(String name) {
		Object object = Objects.requireNonNull(config.get(name), name);
		if (object instanceof List) {
			return (List<?>) object;
		}
		throw new IllegalArgumentException("List expected: " + name);
	}
	
	public ConfigHelper getNode(String name, Map<String, Object> def) {
		if (config.get(name) == null) {
			return new ConfigHelper(def);
		} else {
			return getNode(name);
		}
	}
	

	public ConfigHelper getNode(String name) {
		Object object = Objects.requireNonNull(config.get(name), name);
		if (object instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) object;
			return new ConfigHelper(map);
		}
		throw new IllegalArgumentException("Map expected: " + name);
	}

	public Collection<String> getKeys() {
		return config.keySet();
	}
}
