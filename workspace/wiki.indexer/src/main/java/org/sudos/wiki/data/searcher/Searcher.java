package org.sudos.wiki.data.searcher;

import java.util.List;

/**
 * Retrieves user-readable scored list of document titles.
 * 
 * @author iv
 * 
 */
public interface Searcher {

	/**
	 * returns document titles for given query
	 * 
	 * @param query
	 * @return
	 * @throws SearcherException
	 */
	List<String> getDocumentTitles(String query) throws SearcherException;

}
