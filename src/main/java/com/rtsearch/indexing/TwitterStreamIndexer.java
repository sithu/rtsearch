/**
 * 
 */
package com.rtsearch.indexing;

import java.io.IOException;

import com.rtsearch.twitter.TwitterStatusListener;

import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * @author saung
 *
 */
public class TwitterStreamIndexer {
	/**
	 * Execute Twitter firehose streaming client.
	 * 
	 * @param args
	 * @throws TwitterException
	 * @throws IOException
	 */
	public static void main(String[] args) throws TwitterException, IOException {
		System.out.println("Starting Twitter Streaming...");
		System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict/");
		Indexer indexer = new TweetIndexer("/tmp/lucene");
	    TwitterStream twitterStream = new TwitterStreamFactory(new TwitterStatusListener(indexer)).getInstance("rtsearcher", "msproject");
	    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    twitterStream.sample();
	}
	
}
