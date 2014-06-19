package org.sudos.wiki.data.searcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Posting;

/**
 * @author iv Simply caclulates scores basing on term score and document score
 * 
 */
public class WikiScorer implements Scorer {

	public List<DocRecord> scoreAndSort(Posting posting) {
		TreeMap<Double, List<DocRecord>> scoredDocs = new TreeMap<Double, List<DocRecord>>();

		if (posting == null) {
			return Collections.emptyList();
		}

		for (DocRecord docRecord : posting) {
			docRecord.setFinalScore(docRecord.getDocumentBoost()
					* docRecord.getTermScore());
			if (!scoredDocs.containsKey(docRecord.getFinalScore())) {
				scoredDocs.put(docRecord.getFinalScore(),
						new LinkedList<DocRecord>());
			}
			scoredDocs.get(docRecord.getFinalScore()).add(docRecord);
		}

		List<DocRecord> result = new ArrayList<DocRecord>(scoredDocs.size());

		for (List<DocRecord> docRecord : scoredDocs.values()) {
			result.addAll(docRecord);
		}
		Collections.reverse(result);
		return result;

	}
}
