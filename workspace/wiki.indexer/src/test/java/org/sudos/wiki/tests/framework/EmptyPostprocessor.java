package org.sudos.wiki.tests.framework;

import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.indexer.IndexingException;
import org.sudos.wiki.data.model.Document;

public class EmptyPostprocessor implements DataPostprocessor<Document> {

	private Document doc = null;

	public Document postprocess(Document data) throws IndexingException {
		this.doc = data;
		return data;
	}

	public Document getDoc() {
		return doc;
	}
}
