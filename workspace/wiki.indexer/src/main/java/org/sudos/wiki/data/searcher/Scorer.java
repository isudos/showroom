package org.sudos.wiki.data.searcher;

import java.util.List;

import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Posting;

/**
 * @author iv calculates scores and sorts search results
 * 
 */
public interface Scorer {

	List<DocRecord> scoreAndSort(Posting posting);

}
