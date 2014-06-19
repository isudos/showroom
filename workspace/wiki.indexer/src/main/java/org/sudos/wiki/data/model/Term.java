package org.sudos.wiki.data.model;

/**
 * @author iv Term abstraction as it is supposed to have in an inverted index
 * 
 */
public class Term {

	private int docFrequency = 0;
	private Token token;
	private String fieldName;
	private double TermScore;

	public Term(Token token, String fieldName, int docFrequency,
			double termScore) {
		this.token = token;
		this.fieldName = fieldName;
	}

	public Token getToken() {
		return token;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void incrementFrequency() {
		docFrequency++;
	}

	public int getDocFrequency() {
		return docFrequency;
	}

	public double getTermScore() {
		return TermScore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc) compare only token as it is a key
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Term other = (Term) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Term [docFrequency=" + docFrequency + ", token=" + token
				+ ", fieldName=" + fieldName + "]";
	}

}
