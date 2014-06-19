package org.sudos.wiki.data.indexer;

import org.sudos.wiki.data.model.Document;

/**
 * @author iv Indexes model Document
 * 
 */
public interface Indexer {

	/**
	 * This method is responsible for mapping higher-level model Document to
	 * some index internals, operating Index abstraction. In other words - just
	 * indexes the document.
	 * 
	 * @param doc
	 *            - document to be indexed
	 * @throws IndexingException
	 */
	void indexData(Document doc) throws IndexingException;

}
