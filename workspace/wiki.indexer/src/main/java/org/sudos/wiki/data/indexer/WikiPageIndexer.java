package org.sudos.wiki.data.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.index.IndexFactory;
import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.data.model.Index;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

/**
 * @author iv Indexer for fulfilled Document. It indexes documents in each
 *         segment in parallel fashion. For each segment a single thread is
 *         created. It exits only after all the task are completed to prevent
 *         unbound number of threads.
 * 
 */
public class WikiPageIndexer extends AbstractDataPostprocessor<Document>
		implements Indexer {

	private static final double FREQUENCY_BOOST = 10.0;

	private final IndexFactory indexFactory;

	private final ExecutorService executor = Executors.newFixedThreadPool(37);

	public WikiPageIndexer(IndexFactory indexFactory,
			DataPostprocessor<Document> delegate) {
		super(delegate);
		this.indexFactory = indexFactory;

	}

	public void indexData(final Document doc) throws IndexingException {

		final int numberOfThreads = doc.getSegmentedTermsMap().size();
		if (numberOfThreads > 0) {

			List<Callable<Boolean>> indexingTasks = new ArrayList<Callable<Boolean>>(
					numberOfThreads);
			for (final String segment : doc.getSegmentedTermsMap().keySet()) {
				Callable<Boolean> task = new Callable<Boolean>() {

					public Boolean call() throws Exception {
						Index indexSegment = indexFactory.getIndex(segment);
						Map<Term, List<DocRecord>> batch = new HashMap<Term, List<DocRecord>>();
						for (Token tokenKey : doc.getSegmentedTermsMap()
								.get(segment).keySet()) {
							Term t = doc.getSegmentedTermsMap().get(segment)
									.get(tokenKey);
							batch.put(t, Arrays.asList(new DocRecord(doc
									.getId(), doc.getTitle(), t
									.getDocFrequency() * FREQUENCY_BOOST, doc
									.getDocumentBoost())));

						}
						indexSegment.addBatch(batch);

						return true;
					}
				};
				indexingTasks.add(task);
			}
			try {
				List<Future<Boolean>> readiness = executor
						.invokeAll(indexingTasks);
				for (Future<Boolean> future : readiness) {
					future.get();
				}

			} catch (Exception e) {
				throw new IndexingException(
						"Can't proceed parallel indexing with number of segments/threads: "
								+ numberOfThreads, e);
			}
		}

	}

	@Override
	protected Document doPostprocessingStep(Document doc)
			throws IndexingException {

		indexData(doc);

		return doc;
	}

}
