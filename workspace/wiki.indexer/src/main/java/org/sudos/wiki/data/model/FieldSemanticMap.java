package org.sudos.wiki.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iv Maps which field are considered to be structured, or not; Defines
 *         boosts of fields. TODO: complete this class and add to the
 *         postprocessing flow
 */
public class FieldSemanticMap {

	private Map<String, Boolean> nameToStructuring = new HashMap<String, Boolean>();

	public Boolean isStructured(Object key) {
		return nameToStructuring.get(key);
	}

	public void put(String key, Boolean value) {
		nameToStructuring.put(key, value);
	}

	public boolean containsField(String key) {
		return nameToStructuring.containsKey(key);
	}

	public static FieldSemanticMap defaultInstance() {
		FieldSemanticMap result = new FieldSemanticMap();
		result.nameToStructuring.put("title", true);
		result.nameToStructuring.put("text", false);
		result.nameToStructuring.put("model", true);
		return result;
	}

}
