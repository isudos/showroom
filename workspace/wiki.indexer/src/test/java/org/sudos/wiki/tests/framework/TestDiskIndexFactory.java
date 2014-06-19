package org.sudos.wiki.tests.framework;

import org.junit.Ignore;
import org.sudos.wiki.data.index.IndexFactory;
import org.sudos.wiki.data.index.OnDiskIndex;
import org.sudos.wiki.data.model.Index;

@Ignore
public class TestDiskIndexFactory implements IndexFactory {

	private String dir = "";

	public TestDiskIndexFactory(String dir) {
		this.dir = dir;
	}

	public Index getIndex(String segment) {
		OnDiskIndex indx = new OnDiskIndex(segment + "TEST_IX", dir);
		return indx;
	}
}
