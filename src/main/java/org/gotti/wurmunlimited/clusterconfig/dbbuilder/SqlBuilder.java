package org.gotti.wurmunlimited.clusterconfig.dbbuilder;

import java.io.Writer;
import java.util.regex.Pattern;

public class SqlBuilder {

	private Writer writer;

	public SqlBuilder(Writer writer) {
		this.writer = writer;
	}

	public SqlStatementBuilder prepareStatement(String string) {
		String[] parts = string.split(Pattern.quote("?"));
		return new SqlStatementBuilder(parts, writer);
	}

}
