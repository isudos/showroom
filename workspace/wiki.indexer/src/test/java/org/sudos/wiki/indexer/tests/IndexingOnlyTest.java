package org.sudos.wiki.indexer.tests;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.crude.CrudeDataImporter;
import org.sudos.wiki.data.crude.WikiDumpDataImporter;
import org.sudos.wiki.data.indexer.WikiPageIndexer;
import org.sudos.wiki.data.indexer.postprocessors.BookTextTokenizer;
import org.sudos.wiki.data.indexer.postprocessors.DocumentTermCompiler;
import org.sudos.wiki.data.indexer.postprocessors.WikiTextTokenizer;
import org.sudos.wiki.data.tests.Names;
import org.sudos.wiki.tests.framework.Harness;
import org.sudos.wiki.tests.framework.TestDiskIndexFactory;

public class IndexingOnlyTest extends Harness {

	@Test
	public void test() throws Exception {
		CrudeDataImporter dataImporter = new WikiDumpDataImporter(
				this.getPath(Names.INDEXING_ONLY_FILE));
		File dir = new File(this.getDir(Names.INDEXING_ONLY_FILE));
		for (File f : dir.listFiles()) {
			if (f.getName().contains("TEST_IX"))
				f.delete();
		}
		WikiPageIndexer indexer = new WikiPageIndexer(new TestDiskIndexFactory(
				this.getDir(Names.INDEXING_ONLY_FILE)), null);
		DocumentTermCompiler compiler = new DocumentTermCompiler(indexer);
		BookTextTokenizer bookTokenizer = new BookTextTokenizer(compiler);
		WikiTextTokenizer wikiTokenizer = new WikiTextTokenizer(bookTokenizer);

		dataImporter.importData(wikiTokenizer);
		int counter = 0;
		for (File f : dir.listFiles()) {
			if (f.getName().contains("TEST_IX")) {
				Assert.assertTrue(f.exists());
				f.delete();
				counter++;
			}
		}

		Assert.assertEquals(26 + 10 + 1, counter);

	}
}
