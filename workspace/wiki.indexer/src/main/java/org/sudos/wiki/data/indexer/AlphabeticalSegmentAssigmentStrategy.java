package org.sudos.wiki.data.indexer;

import org.sudos.wiki.data.model.Term;

/**
 * @author iv Distributes terms across segments basing on the first letter in
 *         the term.
 * 
 */
public class AlphabeticalSegmentAssigmentStrategy implements
		SegmentAssigmentStrategy {

	private static final String SPECIAL = "special";
	private static final String DEFAULT = "default";

	public String segment(Term t) {
		if (t == null) {
			return DEFAULT;
		}
		return ((t.getToken().getValue().toCharArray()[0] >= '0' && t
				.getToken().getValue().toCharArray()[0] <= '9') || t.getToken()
				.getValue().toCharArray()[0] >= 'a'
				&& t.getToken().getValue().toCharArray()[0] <= 'z') ? String
				.valueOf(t.getToken().getValue().toCharArray()[0]) : SPECIAL;
	}
}
