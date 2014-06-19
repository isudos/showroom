package org.sudos.wiki.indexer.tests;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.indexer.IndexingException;
import org.sudos.wiki.data.indexer.postprocessors.BookTextTokenizer;
import org.sudos.wiki.data.model.Document;

public class BookTextTokenizerTest {

	private static final String[] expectedResult = { "nothing", "perfect",
			"idea", "subjective", "see", "many", "flaws" };

	@Test
	public void testBookTextTokenizationTrim() throws IndexingException {
		String inputString = "  Nothing is Perfect and the idea   of   it     is subjective I  see many flaws in it  ";
		String[] expectedResult = { "nothing", "perfect", "idea", "subjective",
				"see", "many", "flaws" };

		Document doc = new Document();
		doc.getUnstructuredFields().put("field", inputString);

		BookTextTokenizer tokenizer = new BookTextTokenizer(null);
		tokenizer.postprocess(doc);

		int i = 0;
		for (String string : expectedResult) {
			Assert.assertEquals(string, doc.getUnstructuredTokens()
					.get("field")[i]);
			i++;
		}

	}

	@Test
	public void testBookTextTokenizationNotTrim() throws IndexingException {
		String inputString = "Nothing is Perfect and the idea   of   it     is subjective I  see many flaws in it";
		String[] expectedResult = { "nothing", "perfect", "idea", "subjective",
				"see", "many", "flaws" };

		Document doc = new Document();
		doc.getUnstructuredFields().put("field", inputString);

		BookTextTokenizer tokenizer = new BookTextTokenizer(null);
		tokenizer.postprocess(doc);

		int i = 0;
		for (String string : expectedResult) {
			Assert.assertEquals(string, doc.getUnstructuredTokens()
					.get("field")[i]);
			i++;
		}

	}

	@Test
	public void testBookTextTokenizationEndToken() throws IndexingException {
		String inputString = "on on Nothing is Perfect and the idea   of   it     is subjective I  see many flaws";
		String[] expectedResult = { "nothing", "perfect", "idea", "subjective",
				"see", "many", "flaws" };

		Document doc = new Document();
		doc.getUnstructuredFields().put("field", inputString);

		BookTextTokenizer tokenizer = new BookTextTokenizer(null);
		tokenizer.postprocess(doc);

		int i = 0;
		for (String string : expectedResult) {
			Assert.assertEquals(string, doc.getUnstructuredTokens()
					.get("field")[i]);
			i++;
		}

	}
}
