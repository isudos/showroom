package org.sudos.wiki.data.index;

import org.sudos.wiki.data.model.Index;

/**
 * @author iv Factory provides indexes instances for indexing and searhing
 *         flows. It is supposed to return OnDisk, In-Memory, and combined index
 *         implementations.
 * 
 */
public interface IndexFactory {

	Index getIndex(String segment);
}
