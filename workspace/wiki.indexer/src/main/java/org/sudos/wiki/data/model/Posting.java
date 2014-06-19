package org.sudos.wiki.data.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author iv Posting representation - an entry of inverted index. TODO: To
 *         perform effective multi term queries this class should support sorted
 *         doc list.
 */
public class Posting implements Serializable, Iterable<DocRecord> {

	public static final int MAX_KEY_LENGTH = 50;

	private static final String TERM = "term:";

	private static final String NEW_LINE_SYM = System
			.getProperty("line.separator");

	private static final char POSTING_DELIMETER = '@';

	private static final Pattern DELIMETER_PATTERN = Pattern.compile("@");

	private static final String POSTING = "POSTING[";

	private static final Pattern POSTING_PATTERN = Pattern
			.compile("POSTING\\[");

	private String key;

	double termBoost;

	private List<DocRecord> docs = new LinkedList<DocRecord>();

	public Posting(String key, double termBoost) {
		this.key = key;
		this.termBoost = termBoost;
	}

	public Posting(String key, double termBoost, List<DocRecord> docs) {
		this.key = key;
		this.termBoost = termBoost;
		this.docs = docs;
	}

	/**
	 * Adds new document record to a posting; note that the Posting is not
	 * responsible for a sorting to save time during search. the collection
	 * sorting should be handled by Indexer that uses it.
	 * 
	 * @param e
	 * @return
	 */
	public boolean add(DocRecord e) {
		return docs.add(e);
	}

	public boolean add(Collection<DocRecord> e) {
		return docs.addAll(e);
	}

	public boolean addSorted(DocRecord e) {
		return docs.add(e);
	}

	public boolean addSorted(Collection<DocRecord> e) {
		return docs.addAll(e);
	}

	public String getKey() {
		return key;
	}

	public double getTermBoost() {
		return termBoost;
	}

	public List<DocRecord> getDocs() {
		return docs;
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		sb.append(POSTING);
		sb.append(TERM + key);
		for (DocRecord doc : docs) {
			sb.append(POSTING_DELIMETER);
			sb.append(doc.serialize());
		}

		return sb.toString();
	}

	public static String deserializeKey(String string) {

		char[] keyCrude = new char[MAX_KEY_LENGTH];
		int copyLength = POSTING.length() + TERM.length() + MAX_KEY_LENGTH > string
				.length() ? string.length() : POSTING.length() + TERM.length()
				+ MAX_KEY_LENGTH;
		string.getChars(POSTING.length() + TERM.length(), POSTING.length()
				+ TERM.length() + MAX_KEY_LENGTH, keyCrude, 0);
		int endOfKey = 0;
		for (char c : keyCrude) {
			endOfKey++;
			if (c == POSTING_DELIMETER) {
				break;
			}
		}
		if (endOfKey == 0) {
			return null;
		}

		return new String(keyCrude, 0, endOfKey - 1);
	}

	public static String addDocRecToSerialized(DocRecord docRecord,
			String serializedPosting) {
		String docRecordString = docRecord.serialize();
		StringBuilder result = new StringBuilder();

		// char[] resultingCharArray = new char[serializedPosting.length() + 1
		// + docRecordString.length()];
		// serializedPosting.getChars(0,
		// serializedPosting.length() - POSTING_END.length(),
		// resultingCharArray, 0);
		// resultingCharArray[serializedPosting.length() - POSTING_END.length()]
		// = POSTING_DELIMETER;
		// docRecordString.getChars(0, docRecordString.length(),
		// resultingCharArray,
		// serializedPosting.length() - POSTING_END.length() + 1);
		// POSTING_END.getChars(0, POSTING_END.length(), resultingCharArray,
		// serializedPosting.length() + docRecordString.length() + 1
		// - POSTING_END.length());
		result.append(serializedPosting);
		result.append(POSTING_DELIMETER);
		result.append(docRecordString);
		return result.toString();
	}

	public static Posting deserialize(String string) {
		if (string == null) {
			return null;
		}
		String[] parts = DELIMETER_PATTERN.split(POSTING_PATTERN
				.matcher(string).replaceAll(""));
		String term = parts[0].replace(TERM, "");

		Posting result = new Posting(term, 0.0);

		for (int counter = 1; counter < parts.length; counter++) {
			DocRecord dr = DocRecord.deserialize(parts[counter]);
			if (dr != null)
				result.add(dr);
		}
		return result;
	}

	public Iterator<DocRecord> iterator() {
		return this.docs.iterator();
	}

	@Override
	public String toString() {
		return "Posting [key=" + key + ", termBoost=" + termBoost + ", docs="
				+ docs + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((docs == null) ? 0 : docs.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		long temp;
		temp = Double.doubleToLongBits(termBoost);
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
		Posting other = (Posting) obj;
		if (docs == null) {
			if (other.docs != null)
				return false;
		} else if (!docs.equals(other.docs))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (Double.doubleToLongBits(termBoost) != Double
				.doubleToLongBits(other.termBoost))
			return false;
		return true;
	}

}
