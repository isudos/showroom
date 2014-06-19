package org.sudos.wiki.data.indexer.postprocessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.model.Document;

/**
 * The tokenizer that serves to amend the wiki text full of citations, links,
 * and special characters. Due to simple nature of character data here it would
 * be much more suitable to have LL-grammar parser: it would simply clean up
 * unnecessary entries with a near-linear complexity and won't allocate anything
 * redundant. However, the grammar rules set would be rather cumbersome to
 * introduce it in short time. Instead I use regular expressions.
 * 
 * @author iv
 * 
 */
public class WikiTextTokenizer extends AbstractDataPostprocessor<Document> {

	public WikiTextTokenizer(DataPostprocessor<Document> delegate) {
		super(delegate);
	}

	public static final Pattern WIKI_MARKUP_TO_CLEAN = Pattern
			.compile(
					"(\\{\\{(q|Q)uote)|(\\{\\{([^}]*)\\}\\})|(&lt.*&gt)|(&lt.*&lt)|(\\{\\{Redirect.*\\}\\})"
							+ "|(\\[\\[wikiquote.*\\]\\])	|(==See also==.*)|(==References==.*)|"
							+ "(==Bibliography==.*)|(===Cited in footnotes===.*)|(===Additional references===.*)"
							+ "|(==External links==.*)|(\\W)", Pattern.DOTALL);

	@Override
	protected Document doPostprocessingStep(Document doc) {

		for (String fieldName : doc.getUnstructuredFields().keySet()) {

			String newText = handleField(doc.getUnstructuredFields().get(
					fieldName));
			if (newText != null) {
				doc.getUnstructuredFields().put(fieldName, newText);
			} else {
				doc.getUnstructuredFields().remove(fieldName);
			}
		}

		for (String fieldName : doc.getOtherFields().keySet()) {
			String newText = handleField(doc.getOtherFields().get(fieldName));
			if (newText != null) {
				doc.getOtherFields().put(fieldName, newText);
			}
		}
		return doc;
	}

	private String handleField(String original) {
		if (original == null || original.length() <= 0) {
			return null;
		} else {
			Matcher matcher = WIKI_MARKUP_TO_CLEAN.matcher(original);
			return matcher.replaceAll(" ");
		}
	}
}
