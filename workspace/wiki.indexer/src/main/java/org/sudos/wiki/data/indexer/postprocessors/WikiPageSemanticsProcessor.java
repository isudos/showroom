package org.sudos.wiki.data.indexer.postprocessors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.indexer.IndexingException;
import org.sudos.wiki.data.indexer.SegmentAssigmentStrategy;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.data.model.Document.ArticleQualityE;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

/**
 * @author iv This processor is responsible for delving into wiki text and
 *         finding out: REDIRECTs (a.k.a aliases to this page), article quality
 *         tags, like: Good Article, and trying to detect whether this wiki page
 *         belongs to some category (cluster) by analyzing "See Also" list. The
 *         accessory of the page to the cluster leads to boosting the score, to
 *         be more preferable then outliers.
 * 
 *         If the article is marked as Good then we grant it additional 3000
 *         scores. If the article is marked as Selected then we grant it
 *         additional 10000 scores.
 * 
 *         It is assumed, that if the user is aimed to find a certain topic,
 *         then (s)he enters the direct topic name, say "Airbus A350". If it
 *         matches the title, or some redirect pages titles, then we won a
 *         relevance jack pot and grants it additional 10000 scores.
 * 
 * 
 * 
 */
public class WikiPageSemanticsProcessor extends
		AbstractDataPostprocessor<Document> {

	public static double RIGHT_ON_TARGET_SCORE = 10000;
	public static double GOOD_ARTICLE_SCORE = 3000;
	public static double SELECTED_ARTICLE_SCORE = 10000;

	public static double CLUSTERED_HIT_SCORE = 50;
	public static double CLUSTERED_DOC_SCORE = 10;

	private static final String WIKI_TEXT_FIELD = "text";

	private static final Pattern REDIRECT = Pattern
			.compile("\\{\\{Redirect([^}]*)\\}\\}");
	private static final Pattern REDIRECT_PREFIX = Pattern
			.compile("\\{\\{Redirect");
	private static final Pattern REDIRECT_END = Pattern.compile("\\}\\}");
	private static final Pattern REDIRECT_DELIMETER = Pattern.compile("\\|");

	private static final Pattern GOOD_ARTICLE = Pattern
			.compile("\\{\\{\\s*(g|G)ood\\s*(a|A)rticle\\s*\\}\\}");
	private static final Pattern SELECTED_ARTICLE = Pattern
			.compile("\\{\\{\\s*(s|S)elected\\s*(a|A)rticle\\s*\\}\\}");

	private static final Pattern SEE_ALSO = Pattern.compile("==See also==",
			Pattern.DOTALL);
	private static final Pattern SEE_ALSO_END = Pattern.compile("==|$",
			Pattern.DOTALL);

	private static final Pattern SEE_ALSO_ENTRY = Pattern
			.compile("\\[\\[([^]]*)\\]\\]");

	private static final Pattern SEE_ALSO_ENTRY_PREFIX = Pattern
			.compile("\\[\\[");
	private static final Pattern SEE_ALSO_ENTRY_END = Pattern.compile("\\]\\]");

	private final SegmentAssigmentStrategy segmentaAssigmentStrategy;

	public WikiPageSemanticsProcessor(DataPostprocessor<Document> delegate,
			SegmentAssigmentStrategy assigmentStrategy) {
		super(delegate);
		this.segmentaAssigmentStrategy = assigmentStrategy;
	}

	@Override
	protected Document doPostprocessingStep(Document doc)
			throws IndexingException {

		handleRedirect(doc);
		Term titleTerm = new Term(new Token(doc.getTitle()), "title", 1,
				RIGHT_ON_TARGET_SCORE);
		doc.addTerm(segmentaAssigmentStrategy.segment(titleTerm), titleTerm);

		handleQuality(doc);

		// rather untrustful, since hand made matching optimization
		try {
			handleSeeAlso(doc);
		} catch (Exception ex) {
			System.out.println("No clustering available for doc "
					+ doc.getTitle());
		}

		return doc;
	}

	private void handleRedirect(Document doc) {
		Matcher redirectMatcher = REDIRECT.matcher(doc.getUnstructuredFields()
				.get(WIKI_TEXT_FIELD));
		if (redirectMatcher.find()) {
			String redirectString = redirectMatcher.group(0);
			redirectString = REDIRECT_PREFIX.matcher(redirectString)
					.replaceFirst("");
			redirectString = REDIRECT_END.matcher(redirectString).replaceFirst(
					"");
			String[] redirectPages = REDIRECT_DELIMETER.split(redirectString);
			for (String page : redirectPages) {
				if (page.length() > 0) {
					Term t = new Term(new Token(page.trim()), WIKI_TEXT_FIELD,
							1, RIGHT_ON_TARGET_SCORE);
					doc.addTerm(this.segmentaAssigmentStrategy.segment(t), t);
				}

			}
		}
	}

	private void handleQuality(Document doc) {
		Matcher goodArticleMatcher = GOOD_ARTICLE.matcher(doc
				.getUnstructuredFields().get(WIKI_TEXT_FIELD));
		Matcher selectedArticleMatcher = SELECTED_ARTICLE.matcher(doc
				.getUnstructuredFields().get(WIKI_TEXT_FIELD));

		if (selectedArticleMatcher.find()) {
			doc.setArticleQuality(ArticleQualityE.SELECTED);
			doc.increaseBoost(SELECTED_ARTICLE_SCORE);
		}

		else if (goodArticleMatcher.find()) {
			doc.setArticleQuality(ArticleQualityE.GOOD);
			doc.increaseBoost(GOOD_ARTICLE_SCORE);
		}
	}

	private void handleSeeAlso(Document doc) {
		Matcher seeAlsoMatcher = SEE_ALSO.matcher(doc.getUnstructuredFields()
				.get(WIKI_TEXT_FIELD));
		if (seeAlsoMatcher.find()) {

			Matcher seeAlsoEnd = SEE_ALSO_END.matcher(doc
					.getUnstructuredFields().get(WIKI_TEXT_FIELD));
			int start = seeAlsoMatcher.start(0) + SEE_ALSO.pattern().length()
					+ 1;
			seeAlsoEnd.find(start);
			int end = seeAlsoEnd.start(0);
			char[] copied = new char[end - start + 1];
			doc.getUnstructuredFields()
					.get(WIKI_TEXT_FIELD)
					.getChars(
							seeAlsoMatcher.start(0)
									+ SEE_ALSO.pattern().length() + 1,
							seeAlsoEnd.start(0), copied, 0);

			String seeAlsoString = new String(copied);
			Matcher seeAlsoEntryMatcher = SEE_ALSO_ENTRY.matcher(seeAlsoString);
			List<String> seeAlsoTerms = new ArrayList<String>();
			int count = 0;
			while (seeAlsoEntryMatcher.find()) {
				seeAlsoTerms.add(seeAlsoEntryMatcher.group(1));
				count++;
			}
			for (String seeAlsoTerm : seeAlsoTerms) {
				Term t = new Term(new Token(seeAlsoTerm), "seeAlso", 1,
						CLUSTERED_HIT_SCORE * count);

				doc.addTerm(this.segmentaAssigmentStrategy.segment(t), t);
			}
			doc.increaseBoost(CLUSTERED_DOC_SCORE * count);
		}
	}
}
