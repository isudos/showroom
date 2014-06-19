package org.sudos.wiki.searcher.tests;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.crude.CrudeDataImporter;
import org.sudos.wiki.data.crude.WikiDumpDataImporter;
import org.sudos.wiki.data.index.IndexFactory;
import org.sudos.wiki.data.indexer.AlphabeticalSegmentAssigmentStrategy;
import org.sudos.wiki.data.indexer.WikiPageIndexer;
import org.sudos.wiki.data.indexer.postprocessors.BookTextTokenizer;
import org.sudos.wiki.data.indexer.postprocessors.DocumentTermCompiler;
import org.sudos.wiki.data.indexer.postprocessors.WikiTextTokenizer;
import org.sudos.wiki.data.searcher.Searcher;
import org.sudos.wiki.data.searcher.SegmentedSearcher;
import org.sudos.wiki.data.searcher.WikiScorer;
import org.sudos.wiki.data.tests.Names;
import org.sudos.wiki.tests.framework.Harness;
import org.sudos.wiki.tests.framework.TestDiskIndexFactory;

public class BasicSearcherTest extends Harness {

	@Test
	public void testSearch() throws Exception {
		CrudeDataImporter dataImporter = new WikiDumpDataImporter(
				this.getPath(Names.BASIC_SEARCH_INPUT_DATA));
		File dir = new File(this.getDir(Names.BASIC_SEARCH_INPUT_DATA));
		for (File f : dir.listFiles()) {
			if (f.getName().contains("TEST_IX"))
				f.delete();
		}

		IndexFactory indexFactory = new TestDiskIndexFactory(
				this.getDir(Names.BASIC_SEARCH_INPUT_DATA));
		WikiPageIndexer indexer = new WikiPageIndexer(indexFactory, null);
		DocumentTermCompiler compiler = new DocumentTermCompiler(indexer);
		BookTextTokenizer bookTokenizer = new BookTextTokenizer(compiler);
		WikiTextTokenizer wikiTokenizer = new WikiTextTokenizer(bookTokenizer);

		dataImporter.importData(wikiTokenizer);

		Searcher searcher = new SegmentedSearcher(
				new AlphabeticalSegmentAssigmentStrategy(), indexFactory,
				new WikiScorer());

		List<String> anarchism = searcher.getDocumentTitles("anarchism");
		Assert.assertEquals(1, anarchism.size());
		Assert.assertTrue(anarchism.get(0).contains("Anarchism"));

		List<String> convex = searcher.getDocumentTitles("convex");
		Assert.assertEquals(1, convex.size());
		Assert.assertTrue(convex.get(0).contains("Convex set"));

		List<String> tiger = searcher.getDocumentTitles("Tiger");
		Assert.assertEquals(1, tiger.size());
		Assert.assertTrue(tiger.get(0).contains(
				"Crouching Tiger, Hidden Dragon"));

		List<String> vulcano = searcher.getDocumentTitles("Eyjafjallajökull");
		Assert.assertEquals(0, vulcano.size());

		for (File f : dir.listFiles()) {
			if (f.getName().contains("TEST_IX")) {
				f.delete();
			}
		}
	}
}
