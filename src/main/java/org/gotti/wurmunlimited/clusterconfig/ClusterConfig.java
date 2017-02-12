package org.gotti.wurmunlimited.clusterconfig;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jvyaml.YAML;

public class ClusterConfig {
	
	private static final Options OPTIONS = new Options()
			.addOption("s", "sql", true, "print setup Sql for a server")
			.addOption("v", "verify", true, "verify setup")
			.addOption("i", "ini", true, "print wurm.ini options"); 

	private Map<String, ClusterNode> clusterNodes;

	public ClusterConfig(Path configFile) {
		load(configFile);
	}

	private void load(Path configFile) {
		try (Reader reader = Files.newBufferedReader(configFile)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) YAML.load(reader);
			ConfigHelper helper = new ConfigHelper(data);

			Map<String, ClusterNode> clusterNodes = new LinkedHashMap<>();

			ConfigHelper servers = helper.getNode("servers");
			for (String serverName : servers.getKeys()) {
				ConfigHelper server = servers.getNode(serverName);
				ClusterNode clusterNode = new ClusterNode(serverName, server);
				clusterNodes.put(serverName, clusterNode);
			}
			
			this.clusterNodes = clusterNodes;
			
			Set<Integer> ids = new HashSet<>();
			this.clusterNodes.forEach((name, node) -> {
				if (node.getNodeType() != NodeType.STANDALONE && !ids.add(node.getId())) {
					throw new IllegalArgumentException("duplicate id " + node.getId());
				}
			});

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ClusterNode getServerConfig(String serverName) {
		if (!this.clusterNodes.containsKey(serverName)) {
			throw new IllegalArgumentException("Missing server configuration for " + serverName);
		}
		return this.clusterNodes.get(serverName);
	}

	public Map<Direction, ClusterNode> getDirections(String serverName) {
		Map<Direction, ClusterNode> directions = new HashMap<>();
		final ClusterNode server = getServerConfig(serverName);
		server.getDirections().forEach((direction, name) -> {
			final ClusterNode neighbour = getServerConfig(name);
			if (neighbour.getNodeType() == NodeType.STANDALONE) {
				throw new IllegalArgumentException("Standalone server used in direction");
			}
			directions.put(direction, Objects.requireNonNull(neighbour));
		});
		
		if (server.getNodeType() == NodeType.STANDALONE && !directions.isEmpty()) {
			throw new IllegalArgumentException("Standalone server has neighbours");
		}
		return directions;
	}

	public List<ClusterNode> getNeighbours(String serverName) {
		List<ClusterNode> neighbours = new ArrayList<>();
		this.clusterNodes.forEach((name, node) -> {
			if (!Objects.equals(name, serverName)) {
				final ClusterNode neighbour = getServerConfig(name);
				if (neighbour.getNodeType() != NodeType.STANDALONE) {
					neighbours.add(neighbour);
				}
			}
		});
		return neighbours;
	}
	
	private static void printIniOption(String option, String value) {
		System.out.println(option + " " + value);
	}
	
	private static void checkIps(ClusterNode node) {
		node.getPublicIp();
		node.getInternalIp();
	}
	
	public static void run(String... args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(OPTIONS, args);
		
		if (cmd.hasOption("s") && cmd.getArgList().size() == 1) {
			
			final String serverName = cmd.getOptionValue("s");

			ClusterConfig config = new ClusterConfig(Paths.get(cmd.getArgList().get(0)));

			ClusterNode serverConfig = config.getServerConfig(serverName);
			System.out.println(new ServerConfigBuilder(serverConfig, true).toString());

			List<ClusterNode> neighbours = config.getNeighbours(serverName);
			if (serverConfig.getNodeType() == NodeType.STANDALONE) {
				System.out.println("DELETE FROM SERVERS WHERE LOCAL = 0;");
			} else {
				neighbours.forEach(node -> System.out.println(new ServerConfigBuilder(node, false).toString()));
			}

			Map<Direction, ClusterNode> directions = config.getDirections(serverName);
			System.out.println(new NeighbourConfigBuilder(serverConfig, directions).toString());
		} else if (cmd.hasOption("v")  && cmd.getArgList().size() == 1) {
			final String serverName = cmd.getOptionValue("v");
			
			ClusterConfig config = new ClusterConfig(Paths.get(cmd.getArgList().get(0)));
			checkIps(config.getServerConfig(serverName));
			config.getNeighbours(serverName).forEach(ClusterConfig::checkIps);
			config.getDirections(serverName);
		} else if (cmd.hasOption("i")  && cmd.getArgList().size() == 1) {
			
			final String serverName = cmd.getOptionValue("i");
			
			ClusterConfig config = new ClusterConfig(Paths.get(cmd.getArgList().get(0)));
			ClusterNode serverConfig = config.getServerConfig(serverName);
			
			serverConfig.getOptions().forEach(ClusterConfig::printIniOption);
			
			printIniOption("USE_INCOMING_RMI", Boolean.toString(serverConfig.isRmiEnabled()));
		} else {
			throw new ParseException("invalid mode");
		}
	}

	public static void main(String... args) {

		try {
			run(args);
		} catch (ParseException e) {
			new HelpFormatter().printHelp(ClusterConfig.class.getName(), OPTIONS);
		} catch (Exception e) {
			Logger.getLogger(ClusterConfig.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			System.exit(-1);
		}
	}
}
