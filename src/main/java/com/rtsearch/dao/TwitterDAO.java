/**
 * 
 */
package com.rtsearch.dao;

/**
 * @author saung
 *
 */
public class TwitterDAO {
	private final RedisDataStore dataStore;
	private static final String RECENT_TIWTTERER_SET = "twitterer:recent";
	private static final String RECENT_SEARCH_KEYWORD_SET = "tweet:recent:search_keyword";
	private static final String TWEET_SCORE_SET = "tweet:score:";
	private static final String RECENT_TWEET_SET = "tweet:recent:";
	
	/**
	 * @param dataStore
	 */
	public TwitterDAO(RedisDataStore dataStore) {
		super();
		this.dataStore = dataStore;
	}
	
	public void addRecentTweet(long tweetId, String tweet) {
		dataStore.set(String.valueOf(RECENT_TIWTTERER_SET + tweetId), tweet);
	}
	
	public void addRecentTwitterer(String screenName) {
		dataStore.addSet(RECENT_TIWTTERER_SET, screenName);
	}
	
	public void addRecentSearchKeyword(String keyword) {
		dataStore.addSet(RECENT_SEARCH_KEYWORD_SET, keyword);
	}
	
	public void addTweetScore(long tweetId, double score) {
		dataStore.addSortedSet(String.valueOf(TWEET_SCORE_SET + tweetId), score, String.valueOf(tweetId));
	}
	
}
