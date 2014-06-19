package org.sudos.wiki.tests.framework;

import java.io.BufferedReader;
import java.io.FileReader;

import org.sudos.wiki.data.model.FieldSemanticMap;

public class Harness {

	public FieldSemanticMap getTestSemanticsMap() {
		FieldSemanticMap result = new FieldSemanticMap();
		result.put("text", false);
		return result;
	}

	public String getPath(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name)
				.getFile();
	}

	public String getDir(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name)
				.getFile().replace(name, "");
	}

	public String getFileContent(String name) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				getPath(name)));
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[512];
		int numRead = 0;
		while ((numRead = bufferedReader.read(buffer)) != -1) {
			String readData = String.valueOf(buffer, 0, numRead);
			sb.append(readData);
		}

		bufferedReader.close();
		return sb.toString();

	}

}
