/**
 * 
 */
package com.rtsearch.indexing;

import java.io.IOException;

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

/**
 * @author saung
 *
 */
public class TweetIndexer implements Indexer {

	public static final String INDEX_DIRECTORY = "indexDirectory";

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";
	
	private final Analyzer analyzer;
	private final boolean recreateIndexIfExists;
	private final IndexWriter indexWriter;
	
	public TweetIndexer() {
		this.analyzer = new StandardAnalyzer(Version.LUCENE_30);
		this.recreateIndexIfExists = true;
		this.indexWriter = getIndexWriter();
	}
	
	private IndexWriter getIndexWriter() {
		Directory dir = new RAMDirectory();
		try {
			return new IndexWriter(dir, analyzer, recreateIndexIfExists, IndexWriter.MaxFieldLength.UNLIMITED);
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
	public void createIndex(String content) {
		final Document doc = new Document();
		doc.add(new Field(FIELD_CONTENTS, content, Field.Store.YES, Field.Index.ANALYZED));
		
		try {
			this.indexWriter.addDocument(doc);
			this.indexWriter.commit();
			System.out.println("===" + this.indexWriter.numDocs() + " indexes created ==");
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
