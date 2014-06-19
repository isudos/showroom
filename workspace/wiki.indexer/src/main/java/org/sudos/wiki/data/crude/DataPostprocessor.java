package org.sudos.wiki.data.crude;

import org.sudos.wiki.data.indexer.IndexingException;

/**
 * @author iv
 * 
 *         processes any kind of imported data
 * 
 * @param <TDATA>
 */
public interface DataPostprocessor<TDATA> {

	/**
	 * postprocess the data. Say, we have a document with initially fulfilled
	 * fields, then the postprocess can enrich the document with boosting and
	 * more detailed analyzed fields.
	 * 
	 * @param data
	 * @return
	 * @throws IndexingException
	 */
	TDATA postprocess(TDATA data) throws IndexingException;

}
