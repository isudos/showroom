package org.sudos.wiki.data.crude;

import org.sudos.wiki.data.indexer.IndexingException;

/**
 * @author iv
 * 
 *         A processor that is used within a chain of several ohter.
 * 
 * @param <TDATA>
 */
public abstract class AbstractDataPostprocessor<TDATA> implements
		DataPostprocessor<TDATA> {

	private DataPostprocessor<TDATA> delegate;

	public AbstractDataPostprocessor(DataPostprocessor<TDATA> delegate) {
		this.delegate = delegate;
	}

	public TDATA postprocess(TDATA data) throws IndexingException {
		TDATA result = doPostprocessingStep(data);
		if (delegate != null)
			return delegate.postprocess(result);
		else
			return result;
	}

	/**
	 * The body of data processing
	 * 
	 * @param data
	 * @return
	 * @throws IndexingException
	 */
	protected abstract TDATA doPostprocessingStep(TDATA data)
			throws IndexingException;

}
