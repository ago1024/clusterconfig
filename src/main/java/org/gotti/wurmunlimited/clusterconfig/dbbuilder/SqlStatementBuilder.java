package org.gotti.wurmunlimited.clusterconfig.dbbuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

public class SqlStatementBuilder {

	private String[] parts;
	private String[] parameters;
	private Writer writer;

	public SqlStatementBuilder(String[] parts, Writer writer) {
		this.parts = parts;
		this.parameters = new String[parts.length - 1];
		this.writer = writer;
	}

	public void setInt(int i, int value) {
		setParameter(i, String.valueOf(value));
	}

	public void setString(int i, String value) {
		if (value == null)
			setParameter(i, "NULL");
		else
			setParameter(i, "'" + value.replaceAll(Pattern.quote("'"), "''") + "'");
	}

	public void setBoolean(int i, boolean value) {
		if (value) {
			setParameter(i, "1");
		} else {
			setParameter(i, "0");
		}
	}

	public void setByte(int i, byte value) {
		setParameter(i, Byte.toString(value));
	}
	
	private void setParameter(int i, String parameter) {
		if (parameters[i - 1] != null) {
			throw new IllegalArgumentException("Setting parameter " + i + " twice");
		}
		parameters[i - 1] = parameter;
	}

	public void executeUpdate() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			b.append(parts[i]);
			if (i < parameters.length) {
				if (parameters[i] == null) {
					throw new RuntimeException("Parameter " + (i + 1) + " was not set");
				}
				b.append(parameters[i]);
			}
		}
		b.append("\n");
		try {
			writer.write(b.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
