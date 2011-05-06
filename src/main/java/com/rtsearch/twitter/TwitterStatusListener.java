/**
 * 
 */
package com.rtsearch.twitter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.rtsearch.dao.RedisDataStore;
import com.rtsearch.dao.TwitterDAO;
import com.rtsearch.indexing.Indexer;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * @author saung
 *
 */
public class TwitterStatusListener implements StatusListener {
	private static final String LOG_SEPARATOR = ",";
	private static final String HTTP_HTTPS_REGEX = "(http|https)://.+";
	/**
	 * 
	 */
	private Indexer indexer;
	private static final WordNetDatabase database = WordNetDatabase.getFileInstance();
	private final TokenizerFactory searchTermTokenizerFactory = new RegExTokenizerFactory("#[a-zA-Z][a-zA-Z0-9]+");
	private final TwitterDAO dao;
	/**
	 * @param indexer
	 */
	public TwitterStatusListener(Indexer indexer) {
		super();
		this.indexer = indexer;
		this.dao = new TwitterDAO(new RedisDataStore());
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onException(java.lang.Exception)
	 */
	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	@Override
	public void onStatus(Status status) {
		float score = calculateTweetScore(status.isFavorited(), status.isRetweet(), status.getUser().getFollowersCount(), status.getUser().getFriendsCount(), 0);
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(status.getUser().getId()).append(LOG_SEPARATOR)
			.append(status.getUser().getScreenName()).append(LOG_SEPARATOR)
			.append(status.getUser().isGeoEnabled() ? "GEO" : "NO_GEO").append(LOG_SEPARATOR)  
			.append(status.isFavorited()).append(LOG_SEPARATOR)
			.append(status.isRetweet()).append(LOG_SEPARATOR)   
			.append(status.getUser().isStatusFavorited()).append(LOG_SEPARATOR) 
			.append(status.getUser().getFollowersCount()).append(LOG_SEPARATOR)
			.append(status.getUser().getFriendsCount()).append(LOG_SEPARATOR)
			.append(searchTermTokenizer(status.getText())).append(LOG_SEPARATOR) 
			.append(urlTokenizer(status.getText())).append(LOG_SEPARATOR)
			.append(score).append(LOG_SEPARATOR)
			.append(status.getText());
		
        System.out.println(sb.toString());
        
		this.indexer.createIndex(status.getText(), status.getUser().getProfileImageURL(), score);
		
		// userNameTokenizer(status.getText());
		// searchTermTokenizer(status.getText());
		// urlTokenizer(status.getText());
		// isInDictionaryTokenizer(status.getText());
		// dao.addRecentTwitterer(status.getUser().getScreenName());
		// dao.addTweetScore(status.getId(), calculateTweetScore(status.isFavorited(), status.isRetweet()));
		//dao.addRecentTweet(status.getId(), status.getText());
	
		
	}

	private float calculateTweetScore(boolean isFavorited, boolean isRetweet, int numFollower, int numFriends, long createdAt) {
		float score = 0;
		if(isFavorited) {
			score += 1 * 0.2;
		}
		if(isRetweet) {
			score += 1 * 0.2;
		}
		
		score += numFollower * 0.2;
		
		score += numFriends * 0.1;
		
		// score += createdAt * 0.3;
		
		return score;
	}
	
	private void userNameTokenizer(String str) {
		String regex = "@[a-zA-Z0-9]+";
		TokenizerFactory tf = new RegExTokenizerFactory(regex);
		Tokenizer tokenizer = tf.tokenizer(str.toCharArray(), 0, str.length());
		Iterator<String> it = tokenizer.iterator();
		while(it.hasNext()) {
			System.out.println("LINKS_USERS>>>" + it.next());
		}
	}

	/**
	 * Sanitize keywords and filter non-English keywords.
	 * 
	 * @param str - a raw search keyword from the tweet.
	 */
	private boolean searchTermTokenizer(String str) {
		final Tokenizer tokenizer = this.searchTermTokenizerFactory.tokenizer(str.toCharArray(), 0, str.length());
		final Iterator<String> it = tokenizer.iterator();
		String keyword = null;
		Synset[] synsets = null;
		boolean isFound = false;
		
		while(it.hasNext()) {
			keyword = it.next().substring(1);
			synsets = database.getSynsets(keyword.toLowerCase());
			if(synsets == null || synsets.length == 0) {
				// System.out.println("NOT_IN_DICT=" + keyword ); // + ":" + dao.incrementPopularSearchKeyword(keyword));
			} else { 
				for(Synset s : synsets) {
					if(s.getType().equals(SynsetType.NOUN)) {
						// System.out.println("SEARCH_TERM=" + keyword + ":" + dao.incrementPopularSearchKeyword(keyword));
						dao.incrementPopularSearchKeyword(keyword);
						break;
					}
				}
			}
			isFound = true;
		}
		return isFound;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private boolean urlTokenizer(String str) {
		final TokenizerFactory tf = new RegExTokenizerFactory(HTTP_HTTPS_REGEX);
		final Tokenizer tokenizer = tf.tokenizer(str.toCharArray(), 0, str.length());
		final Iterator<String> it = tokenizer.iterator();
		if(it.hasNext()) {
			return true;
		}
		return false;
	}
	
	private void isInDictionaryTokenizer(String str) {
		
		String temp = null;
		Synset[] synsets = null;
		SynsetType type = null;
		Set<String> unqiueWords = new HashSet<String>();
		boolean isEng = false;
		
		String regex = "[a-zA-Z]{2,}";
		StringTokenizer st = new StringTokenizer(str);
		StringBuilder sb = new StringBuilder();
		while(st.hasMoreTokens()) {
			temp = st.nextToken().trim().toLowerCase();
			if(!temp.matches(regex)) {
				continue;
			}
			if(unqiueWords.contains(temp)) {
				continue;
			}
			unqiueWords.add(temp);
			synsets = database.getSynsets(temp);
			if(synsets == null || synsets.length == 0) {
				if(temp.length() > 3) {
					sb.append(temp + ":");
				}
			} else {
				isEng = true;
			}
		}
		
		if(isEng) {
			System.out.println("##KEYWORDS##" + sb.toString());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub

	}

}
