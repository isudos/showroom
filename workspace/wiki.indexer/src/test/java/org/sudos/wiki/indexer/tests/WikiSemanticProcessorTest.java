package org.sudos.wiki.indexer.tests;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.indexer.AlphabeticalSegmentAssigmentStrategy;
import org.sudos.wiki.data.indexer.postprocessors.WikiPageSemanticsProcessor;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.data.model.Document.ArticleQualityE;
import org.sudos.wiki.tests.framework.Harness;

public class WikiSemanticProcessorTest extends Harness {

	@Test
	public void testDocumentSemanticsProcessing() throws Exception {

		Document testDoc = new Document();

		double epsilon = 0.1;

		testDoc.setTitle("Anarchism");

		testDoc.getUnstructuredFields().put("text",
				this.getFileContent("wikiSemanticsTest.xml"));
		WikiPageSemanticsProcessor processor = new WikiPageSemanticsProcessor(
				null, new AlphabeticalSegmentAssigmentStrategy());
		processor.postprocess(testDoc);

		Assert.assertEquals(ArticleQualityE.GOOD, testDoc.getArticleQuality());

		// use kind of machine zero since working with doubles
		Assert.assertTrue(Math.abs(1
				+ WikiPageSemanticsProcessor.GOOD_ARTICLE_SCORE + 7
				* WikiPageSemanticsProcessor.CLUSTERED_DOC_SCORE
				- testDoc.getDocumentBoost()) <= epsilon);

	}
}
