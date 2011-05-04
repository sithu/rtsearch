/**
 * 
 */
package com.rtsearch.dao;

import java.util.Set;

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
	private static final String POPULAR_SEARCH_KEYWORD_SET = "tweet:popular:search_keyword";
	
	/**
	 * @param dataStore
	 */
	public TwitterDAO(RedisDataStore dataStore) {
		super();
		this.dataStore = dataStore;
	}
	
	/**
	 * 
	 * @param tweetId
	 * @param tweet
	 */
	public void addRecentTweet(long tweetId, String tweet) {
		dataStore.set(String.valueOf(RECENT_TIWTTERER_SET + tweetId), tweet);
	}
	
	public void addRecentTwitterer(String screenName) {
		dataStore.addSet(RECENT_TIWTTERER_SET, screenName);
	}
	
	public void addRecentSearchKeyword(String keyword) {
		dataStore.addSet(RECENT_SEARCH_KEYWORD_SET, keyword);
	}
	
	/**
	 * Store tweet along with its ranking score.
	 * 
	 * @param tweetId - an unique tweet id.
	 * @param score
	 */
	public void addTweetScore(long tweetId, double score) {
		dataStore.addSortedSet(String.valueOf(TWEET_SCORE_SET + tweetId), score, String.valueOf(tweetId));
	}
	
	/**
	 * Add to the popular keyword set if new; otherwise increment the counter by one.
	 * 
	 * @param keyword - a popular keyword
	 * @return the updated frequency counter.
	 */
	public Double incrementPopularSearchKeyword(String keyword) {
		return dataStore.incrementSortedSet(POPULAR_SEARCH_KEYWORD_SET, 1, keyword);
	}
	
	/**
	 * Get popular search keywords.
	 *  
	 * @param min - a minimum hit.
	 * @param max - a maximum hit.
	 * @param limit - a result limit.
	 * @return a set of sorted popular keywords by total number of hit. 
	 */
	public Set<String> getPupularSearchKeywords(double min, double max, int limit) {
		return dataStore.getSortedSetByScore(POPULAR_SEARCH_KEYWORD_SET, min, max, 0, limit);
	}
	
}
