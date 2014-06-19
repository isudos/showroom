package org.sudos.wiki.data.searcher;

import java.util.LinkedList;
import java.util.List;

import org.sudos.wiki.data.index.IndexException;
import org.sudos.wiki.data.index.IndexFactory;
import org.sudos.wiki.data.indexer.SegmentAssigmentStrategy;
import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Index;
import org.sudos.wiki.data.model.Query;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

/**
 * @author iv
 * 
 *         Proceeds search. Aware of index segments.
 * 
 */
public class SegmentedSearcher implements Searcher {

	private final SegmentAssigmentStrategy segmentAssigmentStrategy;

	private final IndexFactory indexFactory;

	private final Scorer scorer;

	public SegmentedSearcher(SegmentAssigmentStrategy segmentAssigmentStrategy,
			IndexFactory indexFactory, Scorer scorer) {
		super();
		this.segmentAssigmentStrategy = segmentAssigmentStrategy;
		this.indexFactory = indexFactory;
		this.scorer = scorer;
	}

	public List<String> getDocumentTitles(String query)
			throws SearcherException {
		query = query.toLowerCase();

		Query expandedQuery = new Query(query);

		// here one can add a code to expand the initial query with, say,
		// synonims[

		// ]

		try {
			String segment = segmentAssigmentStrategy.segment(new Term(
					new Token(query), "any", 0, 0));
			Index index = indexFactory.getIndex(segment);
			index.restore(segment);

			List<DocRecord> sortedDocRecords = scorer.scoreAndSort(index
					.getByKey(expandedQuery));
			List<String> result = new LinkedList<String>();
			// nothing was found, retrun empty list
			if (sortedDocRecords == null) {
				return result;
			}
			for (DocRecord dr : sortedDocRecords) {
				result.add(dr.getHeadLine() + " | with score: "
						+ dr.getFinalScore());
			}
			return result;
		} catch (IndexException e) {
			throw new SearcherException("Can't traverse index", e);
		}
	}
}
