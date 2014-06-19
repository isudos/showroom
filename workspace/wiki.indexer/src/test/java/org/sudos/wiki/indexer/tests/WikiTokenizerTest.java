package org.sudos.wiki.indexer.tests;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.indexer.postprocessors.WikiTextTokenizer;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.tests.framework.Harness;

public class WikiTokenizerTest extends Harness {

	@Test
	public void testMarkupIsRemoved() throws Exception {

		String expectedResult = "it would transform mathematics by allowing a computer to find a formal proof of any theorem which has a proof of a reasonable "
				+ "length since formal proofs can easily be recognized in polynomial time Example problems may well "
				+ "include all of the Clay Math Institute Millennium Prize Problems CMI prize problems A proof that showed that P NP would "
				+ "lack the practical computational benefits of a proof that P NP but would nevertheless represent a very significant advance in computational"
				+ " complexity theory and provide guidance for future research It would allow one to "
				+ "show in a formal way that many common problems cannot be solved efficiently so that the attention of researchers can be"
				+ " focused on partial solutions or solutions to other problems Due to widespread belief"
				+ " in P NP much of this focusing of research has already taken place";

		Document testDoc = new Document();

		testDoc.getUnstructuredFields().put("field",
				this.getFileContent("wikiTokenizerTestData.txt"));

		WikiTextTokenizer tokenizer = new WikiTextTokenizer(null);
		tokenizer.postprocess(testDoc);
		String result = testDoc.getUnstructuredFields().get("field");

		result = result.trim();
		result = result.replaceAll("\\s+", " ");

		Assert.assertEquals(expectedResult, result);
	}
}
