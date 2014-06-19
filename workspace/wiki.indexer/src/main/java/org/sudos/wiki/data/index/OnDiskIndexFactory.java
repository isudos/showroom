package org.sudos.wiki.data.index;

import org.sudos.wiki.data.model.Index;

/**
 * @author iv Produces on disk index instances. Once can specify base directory
 *         on file system where index will be placed.
 * 
 */
public class OnDiskIndexFactory implements IndexFactory {

	private String baseDirectory = "";

	public OnDiskIndexFactory() {
	}

	public OnDiskIndexFactory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public Index getIndex(String segment) {
		OnDiskIndex indx = new OnDiskIndex(segment, baseDirectory);
		return indx;
	}

}
