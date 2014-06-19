package org.sudos.wiki.data.indexer.postprocessors;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.indexer.AlphabeticalSegmentAssigmentStrategy;
import org.sudos.wiki.data.indexer.SegmentAssigmentStrategy;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

/**
 * @author iv Simply flushes elicited tokens into segmented terms map. every
 *         term addition leads to termFrequency increment.
 * 
 */
public class DocumentTermCompiler extends AbstractDataPostprocessor<Document> {

	private SegmentAssigmentStrategy segmentAssigmentStrategy = new AlphabeticalSegmentAssigmentStrategy();

	public DocumentTermCompiler(DataPostprocessor<Document> delegate) {
		super(delegate);
	}

	@Override
	protected Document doPostprocessingStep(Document doc) {
		for (String unstructuredFiled : doc.getUnstructuredTokens().keySet()) {
			for (String token : doc.getUnstructuredTokens().get(
					unstructuredFiled)) {
				Term t = new Term(new Token(token), unstructuredFiled, 0, 0);
				doc.addTerm(segmentAssigmentStrategy.segment(t), t);
			}
		}
		for (String structuredFiled : doc.getStructuredTokens().keySet()) {
			for (String token : doc.getStructuredTokens().get(structuredFiled)) {
				Term t = new Term(new Token(token), structuredFiled, 0, 0);
				doc.addTerm(segmentAssigmentStrategy.segment(t), t);
			}
		}

		return doc;
	}
}
