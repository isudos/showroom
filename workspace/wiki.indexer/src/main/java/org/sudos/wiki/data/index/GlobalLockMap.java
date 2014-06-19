package org.sudos.wiki.data.index;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iv This class provides a unique locks for each index segment. Used to
 *         allow locking when modification index segment files.
 * 
 */
public class GlobalLockMap {

	private static Map<String, Object> locks = new HashMap<String, Object>();

	synchronized static Object get(Object key) {
		return locks.get(key);
	}

	synchronized static Object checkAndPutIfNecessary(String key) {
		if (!locks.containsKey(key))
			locks.put(key, new Object());
		return locks.get(key);
	}

	synchronized static void clear() {
		locks.clear();
	}

}
