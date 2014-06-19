package org.sudos.wiki.data.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author iv Model representation of a single wiki page to be indexed
 * 
 */
public class Document {

	private UUID id;

	private String title;

	private double documentBoost = 1.0;

	public enum ArticleQualityE {
		STANDARD, GOOD, SELECTED
	};

	private Map<String, String> structuredFields = new LinkedHashMap<String, String>();
	private Map<String, String[]> structuredTokens = new LinkedHashMap<String, String[]>();

	private Map<String, String> unstructuredFields = new LinkedHashMap<String, String>();
	private Map<String, String[]> unstructuredTokens = new LinkedHashMap<String, String[]>();

	private Map<String, String> otherFields = new LinkedHashMap<String, String>();
	private Map<String, String[]> otherTokens = new LinkedHashMap<String, String[]>();

	private List<String> seeAlso = new LinkedList<String>();

	private Map<String, Map<Token, Term>> segmentedTermsMap = new LinkedHashMap<String, Map<Token, Term>>();

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	private ArticleQualityE articleQuality = ArticleQualityE.STANDARD;

	public Map<String, String> getStructuredFields() {
		return structuredFields;
	}

	public Map<String, String> getUnstructuredFields() {
		return unstructuredFields;
	}

	public Map<String, String> getOtherFields() {
		return otherFields;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addTerm(String segment, Term term) {
		if (!this.segmentedTermsMap.containsKey(segment)) {
			this.segmentedTermsMap.put(segment,
					new LinkedHashMap<Token, Term>());
		}
		if (this.segmentedTermsMap.get(segment).containsKey(term.getToken())) {
			this.segmentedTermsMap.get(segment).get(term.getToken())
					.incrementFrequency();
		} else {
			this.segmentedTermsMap.get(segment).put(term.getToken(), term);
			term.incrementFrequency();
		}

	}

	public void addSeeAlso(String value) {
		this.seeAlso.add(value);
	}

	public List<String> getSeeAlso() {
		return seeAlso;
	}

	public void putUnstructuredTokens(String fieldName, String[] tokens) {
		this.unstructuredTokens.put(fieldName, tokens);
	}

	public Map<String, String[]> getStructuredTokens() {
		return structuredTokens;
	}

	public Map<String, String[]> getUnstructuredTokens() {
		return unstructuredTokens;
	}

	public Map<String, String[]> getOtherTokens() {
		return otherTokens;
	}

	public Map<String, Map<Token, Term>> getSegmentedTermsMap() {
		return segmentedTermsMap;
	}

	public double getDocumentBoost() {
		return documentBoost;
	}

	public void setDocumentBoost(double documentBoost) {
		this.documentBoost = documentBoost;
	}

	public ArticleQualityE getArticleQuality() {
		return articleQuality;
	}

	public void setArticleQuality(ArticleQualityE articleQuality) {
		this.articleQuality = articleQuality;
	}

	public void increaseBoost(double boost) {
		this.documentBoost += boost;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", title=" + title + ", documentBoost="
				+ documentBoost + ", structuredFields=" + structuredFields
				+ ", structuredTokens=" + structuredTokens
				+ ", unstructuredFields=" + unstructuredFields
				+ ", unstructuredTokens=" + unstructuredTokens
				+ ", otherFields=" + otherFields + ", otherTokens="
				+ otherTokens + ", seeAlso=" + seeAlso + ", segmentedTermsMap="
				+ segmentedTermsMap + ", articleQuality=" + articleQuality
				+ "]";
	}

}
