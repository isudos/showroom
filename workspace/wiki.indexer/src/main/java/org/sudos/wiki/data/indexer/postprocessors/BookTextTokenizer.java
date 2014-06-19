package org.sudos.wiki.data.indexer.postprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.model.Document;

/**
 * @author iv
 * 
 *         This tokenizer is able to consume a clean book text and produce an
 *         array of tokens, omiting such stop words as articles and
 *         prepositions.
 * 
 */
public class BookTextTokenizer extends AbstractDataPostprocessor<Document> {

	public final static Set<String> STOP_WORDS_SET = new HashSet<String>(
			Arrays.asList("a", "the", "is", "are", "was", "were", "it", "of",
					"on", "and", "at", "in", "to", "from", "do", "does", "not",
					"dont", "doesnt", "have", "has", "i", "she", "he", "they",
					"me", "my"));

	// private final static Pattern STOP_WORDS = Pattern
	// .compile("(\\s((a|A)nd|(T|t)hey|(A|a)re|(I|i)s|(S|s)he|(H|h)e|(I|i)t|(T|t)he|(o|O)f|(t|T)o|(o|O)n|(a|A)t|(f|F)rom|(l|L)e|(l|L)a|(d|D)e|(d|D)u|(a|A)|(i|I)){1}\\s)+");

	// private final static Pattern TOKEN_DELIMETER = Pattern.compile("(\\s+)");

	public BookTextTokenizer(DataPostprocessor<Document> delegate) {
		super(delegate);
	}

	@Override
	protected Document doPostprocessingStep(Document doc) {

		for (String field : doc.getUnstructuredFields().keySet()) {
			handleUnStructuredField(field, doc);
		}
		for (String field : doc.getOtherFields().keySet()) {
			handleOtherField(field, doc);
		}
		return doc;
	}

	private void handleOtherField(String field, Document doc) {

	}

	private void handleUnStructuredField(String field, Document doc) {

		String text = doc.getUnstructuredFields().get(field);
		List<String> extractedTokens = new ArrayList<String>();
		String currentWord;
		int startOfWord = 0;
		boolean inWord = false;

		char[] symbols = text.toCharArray();
		for (int cursor = 0; cursor < symbols.length; cursor++) {
			if (symbols[cursor] == ' ' || cursor == symbols.length - 1) {
				if (inWord) {
					if (cursor == symbols.length - 1) {
						symbols[cursor] = Character
								.toLowerCase(symbols[cursor]);
						cursor++;
					}
					currentWord = String.copyValueOf(symbols, startOfWord,
							cursor - startOfWord);
					if (!STOP_WORDS_SET.contains(currentWord))
						extractedTokens.add(currentWord);
					inWord = false;

				}
			} else {
				symbols[cursor] = Character.toLowerCase(symbols[cursor]);
				if (inWord == false)
					startOfWord = cursor;

				inWord = true;

			}

		}

		//
		// String[] tokens = TOKEN_DELIMETER.split(STOP_WORDS.matcher(
		// doc.getUnstructuredFields().get(field)).replaceAll(" "));
		doc.putUnstructuredTokens(field,
				extractedTokens.toArray(new String[extractedTokens.size()]));
	}
}
