package org.gotti.wurmunlimited.clusterconfig;

import org.junit.Test;

public class ClusterConfigTest {

	@Test
	public void testMainSql() throws Exception {
		ClusterConfig.run("src\\test\\resources\\servers.yml", "--sql", "Adventure");
	}

	@Test
	public void testMainIni() throws Exception {
		ClusterConfig.run("src\\test\\resources\\servers.yml", "--ini", "Adventure");
	}
	
	@Test
	public void testMainVerify() throws Exception {
		ClusterConfig.run("src\\test\\resources\\servers.yml", "--verify", "Adventure");
	}
}
