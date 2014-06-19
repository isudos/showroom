package org.sudos.wiki.engine;

import org.sudos.wiki.data.crude.WikiDumpDataImporter;
import org.sudos.wiki.data.index.IndexFactory;
import org.sudos.wiki.data.index.OnDiskIndexFactory;
import org.sudos.wiki.data.indexer.AlphabeticalSegmentAssigmentStrategy;
import org.sudos.wiki.data.indexer.WikiPageIndexer;
import org.sudos.wiki.data.indexer.postprocessors.BookTextTokenizer;
import org.sudos.wiki.data.indexer.postprocessors.DocumentTermCompiler;
import org.sudos.wiki.data.indexer.postprocessors.WikiPageSemanticsProcessor;
import org.sudos.wiki.data.indexer.postprocessors.WikiTextTokenizer;
import org.sudos.wiki.data.searcher.Searcher;
import org.sudos.wiki.data.searcher.SearcherException;
import org.sudos.wiki.data.searcher.SegmentedSearcher;
import org.sudos.wiki.data.searcher.WikiScorer;

public class WikiSearchEngine implements SearchEngine {

	public void index(String sourcePath) {
		try {
			// data importer - an entry point to import any wiki dump file
			WikiDumpDataImporter dataImporter = new WikiDumpDataImporter(
					sourcePath);

			// Forming a chain of document processors
			IndexFactory indexFactory = new OnDiskIndexFactory();
			// creates index on disk
			WikiPageIndexer indexer = new WikiPageIndexer(indexFactory, null);
			// prepares terms
			DocumentTermCompiler compiler = new DocumentTermCompiler(indexer);
			// tokenizes plain text
			BookTextTokenizer bookTokenizer = new BookTextTokenizer(compiler);
			// removes wiki markup
			WikiTextTokenizer wikiTokenizer = new WikiTextTokenizer(
					bookTokenizer);
			// delves into wiki markup to rank the page and create special terms
			WikiPageSemanticsProcessor wikiSemanticsProcessor = new WikiPageSemanticsProcessor(
					wikiTokenizer, new AlphabeticalSegmentAssigmentStrategy());

			// start importing and indexing
			dataImporter.importData(wikiSemanticsProcessor);
		} catch (Exception e) {
			System.out.println("An error occured during indexing: "
					+ e.toString());
		}
	}

	public String search(String query) {
		Searcher searcher = new SegmentedSearcher(
				new AlphabeticalSegmentAssigmentStrategy(),
				new OnDiskIndexFactory(), new WikiScorer());
		StringBuilder result = new StringBuilder();
		try {
			for (String doc : searcher.getDocumentTitles(query)) {
				result.append(doc);
				result.append(System.getProperty("line.separator") == null ? "\n"
						: System.getProperty("line.separator"));
			}
		} catch (SearcherException e) {
			result.append("ERROR");
			System.out.println("An error occured during search: "
					+ e.toString());

		}
		return result.toString();
	}

	public String stats() {
		return "Not implemented yet";
	}

}
