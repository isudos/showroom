package org.sudos.wiki.data.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author iv Queries represent all the interpretations of what the user wanted
 *         to retrieve.
 * 
 */
public class Query {

	private List<String> variants = new LinkedList<String>();

	/**
	 * @param initial
	 *            - the query as is
	 * 
	 */
	public Query(String initial) {
		variants.add(initial);
	}

	/**
	 * add synonim, stemmed word, spell corrected etc
	 * 
	 * @param variant
	 * 
	 */
	public void addVariant(String variant) {
		this.variants.add(variant);
	}

	public boolean match(Posting posting) {
		for (String variant : variants) {
			if (variant.equals(posting.getKey())) {
				return true;
			}
		}
		return false;
	}

}
