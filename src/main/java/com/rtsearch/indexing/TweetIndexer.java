/**
 * 
 */
package com.rtsearch.indexing;

import java.io.IOException;
import java.net.URL;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.rtsearch.util.LuceneUtil;

/**
 * @author saung
 *
 */
public class TweetIndexer implements Indexer {

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";
	
	private final Analyzer analyzer;
	private final boolean recreateIndexIfExists;
	private final IndexWriter indexWriter;
	
	public TweetIndexer(String indexDirWithPath) {
		this.analyzer = new StandardAnalyzer(Version.LUCENE_30);
		this.recreateIndexIfExists = true;
		this.indexWriter = getIndexWriter(indexDirWithPath);
	}
	
	private IndexWriter getIndexWriter(String dirPath) {
		try {
			return new IndexWriter(LuceneUtil.getIndexDir(dirPath), analyzer, recreateIndexIfExists, IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.rtsearch.indexing.Indexer#createIndex(java.lang.String)
	 */
	@Override
	public void createIndex(String content, URL profileImageUrl, float score) {
		final Document doc = new Document();
		doc.add(new Field(FIELD_CONTENTS, content, Field.Store.YES, Field.Index.ANALYZED));
		// TODO - store non-indexed fields to other persistent database.
		doc.add(new Field("profile_image_url", profileImageUrl.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.setBoost(score);
		try {
			this.indexWriter.addDocument(doc);
			this.indexWriter.commit();
			// System.out.println("===" + this.indexWriter.numDocs() + " indexes created ==");
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void closeIndex() {
		try {
			this.indexWriter.optimize();
			this.indexWriter.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
