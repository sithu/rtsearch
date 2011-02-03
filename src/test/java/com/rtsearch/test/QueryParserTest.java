/**
 * 
 */
package com.rtsearch.test;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.rtsearch.util.LucenceUtil;

import junit.framework.TestCase;


/**
 * @author saung
 *
 */
public class QueryParserTest extends TestCase {
	public void testSearchTweet() throws Exception {
		Directory dir = LucenceUtil.getIndexDir("/tmp/lucene");
		IndexSearcher searcher = new IndexSearcher(dir);
		
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new SimpleAnalyzer());
		
		Query query = parser.parse("website");
		TopDocs docs = searcher.search(query, 10);
		
		System.out.println("Total hits = " + docs.totalHits + ". ScoresDocs size = " + docs.scoreDocs.length);
		
		Document d = null;
		for(int i = 0; i < docs.scoreDocs.length; i++) {
			d = searcher.doc(docs.scoreDocs[i].doc);
			System.out.println("Found = " + d.get("contents"));
		}
	}
}
