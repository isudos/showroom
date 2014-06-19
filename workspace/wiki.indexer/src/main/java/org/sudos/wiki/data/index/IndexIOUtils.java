package org.sudos.wiki.data.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Posting;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

/**
 * @author iv This helper encapsulates index file mutation logic. It is aware of
 *         inverted index model and allows for creation and amending posting
 *         lists in some file. Mutation methods are synchronized: only one
 *         thread can update single index segment.
 * 
 *         To amend posting, it iterates over the file with posting list, till
 *         it finds existing key matching If nothing is found then it adds new
 *         posting
 * 
 */
public class IndexIOUtils {

	// keep this value moderate: it is #1 cause of heap pressure as profiling
	// tells
	private static final int BUFFER_SIZE = 500;
	// prefix to temporary index files
	public static final String TMP = "-tmp";

	private static BufferedReader prepareInputStream(String fileName)
			throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			f.createNewFile();
		}
		FileInputStream fileInputStream = new FileInputStream(fileName);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		return new BufferedReader(new InputStreamReader(dataInputStream),
				BUFFER_SIZE);
	}

	private static BufferedWriter prepareOutputStream(String fileName)
			throws IOException {

		File f = new File(fileName + TMP);
		if (!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(fileName + TMP);
		DataOutputStream dataOutputStream = new DataOutputStream(
				fileOutputStream);
		return new BufferedWriter(new OutputStreamWriter(dataOutputStream),
				BUFFER_SIZE);
	}

	/**
	 * makes temporary file to be primary removes previous primary file
	 * 
	 * @param filename
	 */
	private static void swapFiles(String filename) {
		File f = new File(filename);
		f.delete();
		File fTmp = new File(filename + TMP);
		fTmp.renameTo(f);
	}

	/**
	 * We use this method to simply add a single document entry into posting
	 * within index file
	 * 
	 * @param segment
	 *            - index segment
	 * @param key
	 *            - term (a key of a single posting)
	 * @param docRecord
	 *            - document record to add
	 * @param fileName
	 *            index file
	 * @throws IOException
	 */
	public static void appendDocRecordToIndexFile(String segment, Term key,
			DocRecord docRecord, String fileName) throws IOException {
		synchronized (GlobalLockMap.checkAndPutIfNecessary(segment)) {
			try {
				String strLine;
				StringBuilder fileContent = new StringBuilder();
				BufferedReader bufferedReader = prepareInputStream(fileName);
				BufferedWriter bufferedWriter = prepareOutputStream(fileName);

				boolean isNew = true;
				while ((strLine = bufferedReader.readLine()) != null) {

					// that's an optimization trick: we don't want to
					// deserialize the whole posting, only to check the key
					// (term)
					if (Posting.deserializeKey(strLine).equals(
							key.getToken().getValue())) {
						// that's a second trick: we can add doc record w/o
						// serializing/deserializing
						String toWrite = Posting.addDocRecToSerialized(
								docRecord, strLine);
						bufferedWriter.write(toWrite);
						bufferedWriter.newLine();
						isNew = false;
					} else {
						bufferedWriter.write(strLine);
						bufferedWriter.newLine();
					}
				}

				if (isNew) {
					Posting posting = new Posting(key.getToken().getValue(),
							key.getTermScore());
					posting.addSorted(docRecord);
					bufferedWriter.append(posting.serialize());
					bufferedWriter.newLine();
				}

				bufferedWriter.close();
				bufferedReader.close();

				swapFiles(fileName);
			} catch (Exception e) {
				throw new IOException("Can't amend index file " + fileName, e);
			}

		}

	}

	/**
	 * This method adds a batch of terms and a corresoding document record to a
	 * posting list.
	 * 
	 * @param segment
	 *            index segment
	 * @param fileName
	 *            index file name
	 * @param batch
	 *            maps terms to document records
	 * @throws IOException
	 */
	public static void appendDocRecordBatchToIndex(String segment,
			String fileName, Map<Term, List<DocRecord>> batch)
			throws IOException {

		synchronized (GlobalLockMap.checkAndPutIfNecessary(segment)) {
			try {

				String strLine;
				StringBuilder fileContent = new StringBuilder();
				BufferedReader bufferedReader = prepareInputStream(fileName);
				BufferedWriter bufferedWriter = prepareOutputStream(fileName);

				Set<Term> oldRecords = new HashSet<Term>();
				while ((strLine = bufferedReader.readLine()) != null) {

					// that's an optimization trick: we don't want to
					// deserialize the whole posting, only to check the key
					// (term)
					Term template = new Term(new Token(
							Posting.deserializeKey(strLine)), null, 0, 0);
					if (null != batch.get(template)) {
						oldRecords.add(template);
						String toWrite = strLine;
						for (DocRecord dr : batch.get(template)) {
							toWrite = Posting
									.addDocRecToSerialized(dr, toWrite);
						}
						bufferedWriter.write(toWrite);
						bufferedWriter.newLine();
					} else {
						bufferedWriter.write(strLine);
						bufferedWriter.newLine();
					}
				}

				for (Term term : batch.keySet()) {
					if (!oldRecords.contains(term)) {
						Posting posting = new Posting(term.getToken()
								.getValue(), term.getTermScore());
						posting.addSorted(batch.get(term));
						bufferedWriter.append(posting.serialize());
						bufferedWriter.newLine();
					}
				}

				bufferedWriter.close();
				bufferedReader.close();

				swapFiles(fileName);
			} catch (Exception e) {
				throw new IOException("Can't amend index file " + fileName, e);
			}

		}

	}
}
