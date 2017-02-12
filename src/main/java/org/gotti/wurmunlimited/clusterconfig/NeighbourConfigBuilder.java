package org.gotti.wurmunlimited.clusterconfig;

import java.io.StringWriter;
import java.util.Map;

import org.gotti.wurmunlimited.clusterconfig.dbbuilder.SqlBuilder;
import org.gotti.wurmunlimited.clusterconfig.dbbuilder.SqlStatementBuilder;

public class NeighbourConfigBuilder {
	
	private StringWriter writer;
	private SqlBuilder dbcon;
	private ClusterNode node;
	private Map<Direction, ClusterNode> directions;

	public NeighbourConfigBuilder(ClusterNode node, Map<Direction, ClusterNode> directions) {
		this.node = node;
		this.directions = directions;
		this.writer = new StringWriter();
		this.dbcon = new SqlBuilder(writer);

		registerServer();
	}
	
	private void registerServer() {
		
		SqlStatementBuilder ps2 = dbcon.prepareStatement("DELETE FROM SERVERNEIGHBOURS WHERE SERVER=?;");
		ps2.setInt(1, node.getId());
		ps2.executeUpdate();
		
		directions.forEach(this::registerDirection);
	}
	
	private void registerDirection(Direction direction, ClusterNode neighbour) {
		SqlStatementBuilder ps2 = dbcon.prepareStatement("INSERT INTO SERVERNEIGHBOURS (SERVER, NEIGHBOUR, DIRECTION) VALUES (?, ?, ?);");
		ps2.setInt(1, node.getId());
		ps2.setInt(2, neighbour.getId());
		ps2.setString(3, direction.name());
		ps2.executeUpdate();
	}

	@Override
	public String toString() {
		return writer.toString();
	}


}
