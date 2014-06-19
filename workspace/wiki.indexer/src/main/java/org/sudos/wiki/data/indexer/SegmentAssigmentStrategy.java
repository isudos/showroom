package org.sudos.wiki.data.indexer;

import org.sudos.wiki.data.model.Term;

/**
 * @author iv Segments are partitions of index. They can be files, shards etc.
 *         This strategy determines what segment given term falls in.
 * 
 */
public interface SegmentAssigmentStrategy {

	/**
	 * determines what index segment given term falls in.
	 * 
	 * @param t
	 *            term
	 * @return
	 */
	String segment(Term t);

}
