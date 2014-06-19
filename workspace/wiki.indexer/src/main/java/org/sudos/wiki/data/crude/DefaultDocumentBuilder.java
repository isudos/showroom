package org.sudos.wiki.data.crude;

import java.util.UUID;

import org.sudos.wiki.data.model.Document;

public class DefaultDocumentBuilder implements DocumentBuilder {

	private static final String TITLE = "title";
	private Document billet = new Document();

	public void withStructuredField(String name, String value) {
		billet.getStructuredFields().put(name, value);
	}

	public void withUnstructuredField(String name, String value) {
		billet.getUnstructuredFields().put(name, value);
	}

	public void withOtherField(String name, String value) {
		billet.getOtherFields().put(name, value);
	}

	public void withId(UUID id) {
		billet.setId(id);
	}

	public void withTitle(String title) {
		billet.setTitle(title);
	}

	public Document build() {
		if (billet.getStructuredFields().containsKey(TITLE)) {
			withTitle(billet.getStructuredFields().get(TITLE));
		}
		Document result = billet;
		billet = new Document();
		return result;
	}
}
