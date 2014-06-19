package org.sudos.wiki.data.tests;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.sudos.wiki.data.crude.CrudeDataImporter;
import org.sudos.wiki.data.crude.WikiDumpDataImporter;
import org.sudos.wiki.data.indexer.postprocessors.WikiTextTokenizer;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.tests.framework.EmptyPostprocessor;
import org.sudos.wiki.tests.framework.Harness;

public class WikiDataImporterTest extends Harness {

	@Test
	public void testSingleDocumentImportedCorrectly() throws Exception {
		String text = "In other parts of the world, educational reform has had a number of different meanings.";
		String title = "AccessibleComputing";
		String model = "wikitext";

		CrudeDataImporter dataImporter = new WikiDumpDataImporter(
				getPath(Names.SINGLE_PAGE_FOR_TESTS));
		EmptyPostprocessor pp = new EmptyPostprocessor();
		dataImporter.importData(pp);

		Document result = pp.getDoc();

		Assert.assertEquals(1, result.getUnstructuredFields().size());
		Assert.assertEquals(2, result.getStructuredFields().size());

		Assert.assertEquals(text, result.getUnstructuredFields().get("text")
				.trim());
		Assert.assertEquals(title, result.getStructuredFields().get("title")
				.trim());
		Assert.assertEquals(model, result.getStructuredFields().get("model")
				.trim());

	}

	@Ignore
	@Test
	public void testFullFileImportCorrectly() throws Exception {

		// here we expect that neither exceptions nor out of memory occur.
		CrudeDataImporter dataImporter = new WikiDumpDataImporter(
				getPath(Names.WIKI_DUMP_FOR_UNIT_TESTS));

		dataImporter.importData(new WikiTextTokenizer(null));
	}

}
