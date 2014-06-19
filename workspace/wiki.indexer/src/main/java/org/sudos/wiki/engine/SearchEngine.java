package org.sudos.wiki.engine;

/**
 * @author iv Application Entry point
 * 
 */
public interface SearchEngine {

	void index(String sourcePath);

	String search(String query);

	String stats();
}
