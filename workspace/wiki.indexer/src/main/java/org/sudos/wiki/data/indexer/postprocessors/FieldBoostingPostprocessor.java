package org.sudos.wiki.data.indexer.postprocessors;

import org.sudos.wiki.data.crude.AbstractDataPostprocessor;
import org.sudos.wiki.data.crude.DataPostprocessor;
import org.sudos.wiki.data.model.Document;

/**
 * @author iv TODO
 * 
 */
public class FieldBoostingPostprocessor extends
		AbstractDataPostprocessor<Document> {

	public FieldBoostingPostprocessor(DataPostprocessor<Document> delegate) {
		super(delegate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Document doPostprocessingStep(Document data) {
		// TODO Auto-generated method stub
		return null;
	}

}
