package org.gotti.wurmunlimited.clusterconfig;

import java.io.StringWriter;

import org.gotti.wurmunlimited.clusterconfig.dbbuilder.SqlBuilder;
import org.gotti.wurmunlimited.clusterconfig.dbbuilder.SqlStatementBuilder;

public class ServerConfigBuilder {

	private StringWriter writer;
	private SqlBuilder dbcon;
	private ClusterNode node;
	private boolean local;

	public ServerConfigBuilder(ClusterNode node, boolean local) {
		this.node = node;
		this.local = local;
		this.writer = new StringWriter();
		this.dbcon = new SqlBuilder(writer);

		registerServer();
	}
	
	@Override
	public String toString() {
		return writer.toString();
	}

	private void registerServer() {
		
		SqlStatementBuilder ps2 = dbcon.prepareStatement("INSERT INTO SERVERS(SERVER, NAME, INTRASERVERADDRESS,INTRASERVERPORT,INTRASERVERPASSWORD,EXTERNALIP, EXTERNALPORT, LOCAL) SELECT ?,?,?,?,?,?,?,? WHERE NOT EXISTS (SELECT 1 FROM SERVERS WHERE SERVER=?);");
		ps2.setInt(1, node.getId());
		ps2.setString(2, node.getName());
		ps2.setString(3, this.node.getPublicIp());
		ps2.setString(4, Integer.toString(this.node.getGamePort()));
		ps2.setString(5, this.node.getPassword());
		ps2.setString(6, this.node.getPublicIp());
		ps2.setString(7, Integer.toString(this.node.getGamePort()));
		ps2.setBoolean(8, local);
		ps2.setInt(9, node.getId());
		ps2.executeUpdate();
		
		ps2 = dbcon.prepareStatement("UPDATE SERVERS SET SERVER=?,NAME=?,EXTERNALIP=?,EXTERNALPORT=?,INTRASERVERPASSWORD=?,INTRASERVERADDRESS=?,INTRASERVERPORT=?,ISTEST=?,LOGINSERVER=?,RMIPORT=?,REGISTRATIONPORT=? WHERE SERVER=?;");
		ps2.setInt(1, node.getId());
		ps2.setString(2, node.getName());
		ps2.setString(3, this.node.getPublicIp());
		ps2.setString(4, Integer.toString(this.node.getGamePort()));
		ps2.setString(5, this.node.getPassword());
		ps2.setString(6, this.node.getPublicIp());
		ps2.setString(7, Integer.toString(this.node.getGamePort()));
		ps2.setBoolean(8, this.node.getNodeType().isTestServer());
		ps2.setBoolean(9, this.node.getNodeType().isLoginServer());
		ps2.setString(10, Integer.toString(this.node.getRmiPort()));
		ps2.setString(11, Integer.toString(this.node.getRmiRegistration()));
		ps2.setInt(12, node.getId());
		ps2.executeUpdate();

	}
}