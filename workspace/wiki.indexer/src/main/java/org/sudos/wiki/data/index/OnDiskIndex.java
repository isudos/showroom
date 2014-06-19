package org.sudos.wiki.data.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Index;
import org.sudos.wiki.data.model.Posting;
import org.sudos.wiki.data.model.Query;
import org.sudos.wiki.data.model.Term;

/**
 * @author iv
 * 
 *         File system based implementation of Index. The model of this index is
 *         inverted index. Writes to index files are synchronized. Reads are
 *         concurrent (no locking provided).
 * 
 */
public class OnDiskIndex implements Index {

	public static final String INDEX_FILE_PREFIX = "wiki-index_";
	public static final String INDEX_FILE_EXTENSION = ".windx";

	public static final int MARK_LIMIT = 1000000;

	private String baseDirectory = "";

	private String segment;

	private BufferedReader bufferedReader;
	private FileReader fileReader;

	public OnDiskIndex(String segment, String baseDirectory) {
		this.segment = segment;
		if (baseDirectory != null)
			this.baseDirectory = baseDirectory;
	}

	public void openIndexIfClosed() throws IOException {
		if (bufferedReader == null || !bufferedReader.ready()) {
			fileReader = new FileReader(baseDirectory + INDEX_FILE_PREFIX
					+ segment + INDEX_FILE_EXTENSION);
			bufferedReader = new BufferedReader(fileReader);
			bufferedReader.mark(MARK_LIMIT);
		}
	}

	public Posting goTop() throws IndexException {
		try {
			openIndexIfClosed();
			bufferedReader.reset();
			return Posting.deserialize(bufferedReader.readLine());
		} catch (IOException e) {
			throw new IndexException(
					"Can't rollback to the begging of index in segment: "
							+ segment, e);
		}
	}

	public Posting getNextPosting() throws IndexException {
		try {
			openIndexIfClosed();
			return Posting.deserialize(bufferedReader.readLine());
		} catch (IOException e) {
			throw new IndexException(
					"Can't rollback to the begging of index in segment: "
							+ segment, e);
		}
	}

	public Posting getByKey(Query key) throws IndexException {
		try {
			openIndexIfClosed();
			bufferedReader.reset();
			Posting p;
			do {
				p = Posting.deserialize(bufferedReader.readLine());
			} while (p != null && !key.match(p));
			if (p != null)
				return p;
			return null;
		} catch (IOException e) {
			throw new IndexException(
					"Can't rollback to the begging of index in segment: "
							+ segment, e);
		}
	}

	public synchronized void restore(String segment) {
		try {

			String originalName = baseDirectory + INDEX_FILE_PREFIX + segment
					+ INDEX_FILE_EXTENSION;
			File original = new File(originalName);
			File updated = new File(originalName + IndexIOUtils.TMP);
			if (original.exists() && updated.exists()) {
				original.delete();
				updated.renameTo(original);
			}

		} catch (Exception ex) {
			System.out
					.println("Can't restore index, check if the index process has been ever invoked.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sudos.wiki.data.model.Index#addDocRecord(java.lang.String,
	 * org.sudos.wiki.data.model.DocRecord) hoard the data, then flish.
	 */
	public void addDocRecord(Term key, DocRecord docRecord)
			throws IndexException {
		try {
			IndexIOUtils.appendDocRecordToIndexFile(segment, key, docRecord,
					baseDirectory + INDEX_FILE_PREFIX + segment
							+ INDEX_FILE_EXTENSION);
		} catch (IOException e) {
			throw new IndexException("Batch appending failed", e);
		}
	}

	public void addBatch(Map<Term, List<DocRecord>> batch)
			throws IndexException {
		try {
			IndexIOUtils
					.appendDocRecordBatchToIndex(segment, baseDirectory
							+ INDEX_FILE_PREFIX + segment
							+ INDEX_FILE_EXTENSION, batch);
		} catch (IOException e) {
			throw new IndexException("Batch appending failed", e);
		}
	}
}
