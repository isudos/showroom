package org.sudos.wiki.data.model;

import java.util.List;
import java.util.Map;

import org.sudos.wiki.data.index.IndexException;

/**
 * @author iv Index abstraction. Provides iteration over (inverted) index
 *         entries (postings).
 * 
 *         The index model is supposed to combine inverted index with
 *         segmentation of it on the storage. In the general case, segments can
 *         be a leaves in the B-tree. The simplest segmentation is just
 *         distribution index over fixed number of segments.
 * 
 */
public interface Index {

	/**
	 * returns to the first record in the index
	 * 
	 * @return
	 * @throws IndexException
	 */
	Posting goTop() throws IndexException;

	/**
	 * steps to the next posting
	 * 
	 * @return
	 * @throws IndexException
	 */
	Posting getNextPosting() throws IndexException;

	/**
	 * searches posting by the given Query. Note, that the query can match with
	 * it's any variant.
	 * 
	 * @param key
	 * @return
	 * @throws IndexException
	 */
	Posting getByKey(Query key) throws IndexException;

	/**
	 * Adds a single record Term-->DocRecord
	 * 
	 * @param key
	 * @param docRecord
	 * @throws IndexException
	 */
	void addDocRecord(Term key, DocRecord docRecord) throws IndexException;

	/**
	 * Adds a batch of term-to-docRecords entries
	 * 
	 * @param batch
	 * @throws IndexException
	 */
	void addBatch(Map<Term, List<DocRecord>> batch) throws IndexException;

	/**
	 * fixes a particular index segment, if, say, the indexing process was
	 * Interrupted
	 * 
	 * @param segment
	 */
	void restore(String segment);

}
