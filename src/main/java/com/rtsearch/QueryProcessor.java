/**
 * 
 */
package com.rtsearch;

import java.io.IOException;
import java.util.Formatter;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.rtsearch.util.LuceneUtil;

/**
 * @author saung
 *
 */
public class QueryProcessor {
	private final IndexSearcher searcher;
	private final QueryParser parser;
	
	public static final String JSON = "{ \"screen-name\": \"%1s\", \"profile-img\": \"%2s\", \"tweet\": \"%3s\" }";
	/**
	 * 
	 */
	public QueryProcessor() {
		IndexSearcher indexSearcher = null;
		try {
			final Directory dir = LuceneUtil.getIndexDir("/tmp/lucene");
			indexSearcher = new IndexSearcher(dir);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.searcher = indexSearcher;
		this.parser = new QueryParser(Version.LUCENE_30, "contents", new SimpleAnalyzer());
	}

	/**
	 * Search keyword through the indexer and return top five matching tweets.
	 *   
	 * @param keyword - a keyword to be searched.
	 * @return top file results.
	 * @throws Exception if any failure happened during the operation.
	 */
	public StringBuilder searchTweet(String keyword) throws Exception {
		final Query query = parser.parse(keyword);
		final TopDocs docs = searcher.search(query, 5);
		System.out.println("Total hits = " + docs.totalHits + ". ScoresDocs size = " + docs.scoreDocs.length);
		return parseResult(docs.scoreDocs);
	}
	
	private final StringBuilder parseResult(ScoreDoc[] scoreDocs) {
		final StringBuilder result = new StringBuilder();
		final Formatter formatter = new Formatter(result);
		
		if(scoreDocs == null) {
			result.append("NOT FOUND");
			return result;
		}
		
		Document d = null;
		try {
			for(int i = 0; i < scoreDocs.length; i++) {
				d = searcher.doc(scoreDocs[i].doc);
				formatter.format(JSON, "foo", d.get("profile_image_url"), d.get("contents"));
				
				System.out.println("Found = " + result.toString() + ". score=" + d.getBoost());
				if((i+1) < scoreDocs.length) {
					result.append("|");
				}
			}
			//result.append("]");
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	 }
	
}
