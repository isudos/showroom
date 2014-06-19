package org.sudos.wiki.indexer.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.sudos.wiki.data.index.IndexIOUtils;
import org.sudos.wiki.data.model.DocRecord;
import org.sudos.wiki.data.model.Posting;
import org.sudos.wiki.data.model.Term;
import org.sudos.wiki.data.model.Token;

public class IndexUtilsTest {

	@Test
	public void basicTestAmendingPosting() throws IOException {
		String fileName = "testAmendingPosting.windx";
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}

		Term t = new Term(new Token("stein"), "field", 0, 0);
		DocRecord dr1 = new DocRecord(UUID.randomUUID(), "goose", 0.0, 0.0);
		DocRecord dr2 = new DocRecord(UUID.randomUUID(), "goose", 0.0, 0.0);
		DocRecord dr3 = new DocRecord(UUID.randomUUID(), "duck", 0.0, 0.0);
		DocRecord dr4 = new DocRecord(UUID.randomUUID(), "duck", 0.0, 0.0);
		DocRecord dr5 = new DocRecord(UUID.randomUUID(), "swan", 0.0, 0.0);

		IndexIOUtils.appendDocRecordToIndexFile("s", t, dr1, fileName);
		IndexIOUtils.appendDocRecordToIndexFile("s", t, dr2, fileName);
		IndexIOUtils.appendDocRecordToIndexFile("s", t, dr3, fileName);
		IndexIOUtils.appendDocRecordToIndexFile("s", t, dr4, fileName);
		IndexIOUtils.appendDocRecordToIndexFile("s", t, dr5, fileName);

		Posting p = new Posting("stein", 0);
		p.add(dr1);
		p.add(dr2);
		p.add(dr3);
		p.add(dr4);
		p.add(dr5);

		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));
		String currentStr;
		while ((currentStr = bufferedReader.readLine()) != null) {
			Posting p1 = Posting.deserialize(currentStr);
			Assert.assertEquals(p, p1);
		}

		f.delete();
	}

	@Test
	public void batchTestAmendingPosting() throws IOException {
		String fileName = "testAmendingPosting.windx";
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}
		Map<Term, List<DocRecord>> allAboutBirdsBatch = new HashMap<Term, List<DocRecord>>();

		Term t1 = new Term(new Token("stein"), "field", 0, 0);
		DocRecord dr1 = new DocRecord(UUID.randomUUID(), "goose", 0.0, 0.0);
		DocRecord dr2 = new DocRecord(UUID.randomUUID(), "goose", 0.0, 0.0);
		DocRecord dr3 = new DocRecord(UUID.randomUUID(), "duck", 0.0, 0.0);
		DocRecord dr4 = new DocRecord(UUID.randomUUID(), "duck", 0.0, 0.0);
		DocRecord dr5 = new DocRecord(UUID.randomUUID(), "swan", 0.0, 0.0);

		Term t2 = new Term(new Token("migratory"), "field", 0, 0);

		Posting p1 = new Posting("stein", 0);
		p1.add(dr1);
		p1.add(dr2);
		p1.add(dr3);
		p1.add(dr4);
		p1.add(dr5);

		Posting p2 = new Posting("migratory", 0);
		p2.add(dr4);
		p2.add(dr5);
		p2.add(dr1);
		p2.add(dr2);
		p2.add(dr3);

		allAboutBirdsBatch.put(t1, Arrays.asList(dr1, dr2, dr3, dr4, dr5));
		allAboutBirdsBatch.put(t2, Arrays.asList(dr4, dr5));

		IndexIOUtils.appendDocRecordBatchToIndex("s", fileName,
				allAboutBirdsBatch);

		allAboutBirdsBatch = new HashMap<Term, List<DocRecord>>();
		allAboutBirdsBatch.put(t2, Arrays.asList(dr1, dr2, dr3));

		IndexIOUtils.appendDocRecordBatchToIndex("s", fileName,
				allAboutBirdsBatch);

		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));
		String currentStr;
		currentStr = bufferedReader.readLine();
		Posting res = Posting.deserialize(currentStr);
		Assert.assertEquals(p1, res);
		currentStr = bufferedReader.readLine();
		res = Posting.deserialize(currentStr);
		Assert.assertEquals(p2, res);

		f.delete();
	}
}
