package org.sudos.wiki.data.index;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Index;
import org.sudos.wiki.data.model.Posting;
import org.sudos.wiki.data.model.Query;
import org.sudos.wiki.data.model.Term;

/**
 * @author iv
 * 
 *         This is in memory cached inverted index. TODO: implement completely
 *         and attach to OnDiskIndex
 */
public class CachedIndex implements Index {

	private ConcurrentSkipListMap<String, Posting> cachedPostings = new ConcurrentSkipListMap<String, Posting>();

	private String position;

	public Posting goTop() {
		position = cachedPostings.firstKey();
		return cachedPostings.firstEntry().getValue();
	}

	public Posting getNextPosting() {
		if (position == null) {
			position = cachedPostings.firstKey();
		}

		return cachedPostings.get(position);
	}

	public Posting getByKey(String key) {
		return cachedPostings.get(key);
	}

	public void addDocRecord(String key, DocRecord docRecord) {
		if (!cachedPostings.containsKey(key)) {
			cachedPostings.put(key, new Posting(key, 0.0,
					new LinkedList<DocRecord>()));
		}
		this.cachedPostings.get(key).addSorted(docRecord);
	}

	public Posting getByKey(Query key) throws IndexException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDocRecord(Term key, DocRecord docRecord)
			throws IndexException {
		// TODO Auto-generated method stub

	}

	public void addBatch(Map<Term, List<DocRecord>> batch)
			throws IndexException {
		// TODO Auto-generated method stub

	}

	public void restore() {
		// TODO Auto-generated method stub

	}

	public void restore(String segment) {
		// TODO Auto-generated method stub

	}

}
