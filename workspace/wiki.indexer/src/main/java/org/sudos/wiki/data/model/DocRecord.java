package org.sudos.wiki.data.model;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author iv
 * 
 *         Class represents an entry within a sigle posting. Let have a Posting:
 *         orange --> doc1, doc2, ... Here doc1, doc2 etc are DocRecords
 * 
 */
public class DocRecord implements Comparable<DocRecord>, Serializable {

	private static final String DOC_REC_END = "]";

	private static final String DOC_REC_DELIMETER = "#";

	private static final Pattern DELIMETER_PATTERN = Pattern.compile("#");

	private static final String DOC_RECORD_PREFIX = "DR[";

	private static final Pattern DOC_REC_END_PATTERN = Pattern.compile("\\]");

	private static final Pattern DOC_RECORD_PREFIX_PATTERN = Pattern
			.compile("DR\\[");

	private static final long serialVersionUID = 767052286815217742L;

	private UUID docId;

	private double documentBoost;

	private double termScore;

	private String headLine;

	private double finalScore = 0;

	public DocRecord(UUID docId, String headline, double termScore,
			double documentBoost) {
		this.docId = docId;
		this.documentBoost = documentBoost;
		this.termScore = termScore;
		this.headLine = headline;
	}

	public UUID getDocId() {
		return docId;
	}

	public double getDocumentBoost() {
		return documentBoost;
	}

	public double getTermScore() {
		return termScore;
	}

	public String getHeadLine() {
		return headLine;
	}

	public int compareTo(DocRecord o) {
		if (o.documentBoost == documentBoost)
			return 0;
		if (o.documentBoost > documentBoost)
			return 1;

		return -1;
	}

	public double getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		sb.append(DOC_RECORD_PREFIX);
		sb.append(docId);
		sb.append(DOC_REC_DELIMETER);
		sb.append(headLine);
		sb.append(DOC_REC_DELIMETER);
		sb.append(termScore);
		sb.append(DOC_REC_DELIMETER);
		sb.append(documentBoost);
		sb.append(DOC_REC_END);

		return sb.toString();
	}

	public static DocRecord deserialize(String string) {
		// if the record string is somehow invalid then produce nothing
		if (!string.startsWith(DOC_RECORD_PREFIX)
				|| !string.endsWith(DOC_REC_END))
			return null;

		String[] fields = DELIMETER_PATTERN.split(DOC_REC_END_PATTERN.matcher(
				DOC_RECORD_PREFIX_PATTERN.matcher(string).replaceAll(""))
				.replaceAll(""));
		if (fields.length != 4)
			return null;

		return new DocRecord(UUID.fromString(fields[0]), fields[1],
				Double.valueOf(fields[2]), Double.valueOf(fields[3]));
	}

	@Override
	public String toString() {
		return "DocRecord [docId=" + docId + ", documentBoost=" + documentBoost
				+ ", termScore=" + termScore + ", headLine=" + headLine + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
		long temp;
		temp = Double.doubleToLongBits(documentBoost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((headLine == null) ? 0 : headLine.hashCode());
		temp = Double.doubleToLongBits(termScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocRecord other = (DocRecord) obj;
		if (docId == null) {
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		if (Double.doubleToLongBits(documentBoost) != Double
				.doubleToLongBits(other.documentBoost))
			return false;
		if (headLine == null) {
			if (other.headLine != null)
				return false;
		} else if (!headLine.equals(other.headLine))
			return false;
		if (Double.doubleToLongBits(termScore) != Double
				.doubleToLongBits(other.termScore))
			return false;
		return true;
	}

}
