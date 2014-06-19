package org.sudos.wiki.data.crude;

import org.sudos.wiki.data.indexer.IndexingException;
import org.sudos.wiki.data.model.Document;

/**
 * @author iv
 * 
 *         Importers are responsible for loading data from some source (like xml
 *         file) and then delegating detailed analysis to a chain of
 *         postprocessors
 * 
 */
public interface CrudeDataImporter {

	/**
	 * Imports some data and invokes postprocessor
	 * 
	 * @param topPostprocessor
	 *            the topmost (in a chain, if any) postprocessor
	 * @throws IndexingException
	 */
	void importData(DataPostprocessor<Document> topPostprocessor)
			throws IndexingException;

}
