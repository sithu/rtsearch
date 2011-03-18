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
import com.aliasi.util.Strings;
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
	/**
	 * 
	 */
	private Indexer indexer;
	private static final WordNetDatabase database = WordNetDatabase.getFileInstance();
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
		System.out.println(status.getUser().getScreenName() + "||" + 
				(status.getUser().isGeoEnabled() ? status.getGeoLocation() : "NO_GEO") + "||" + status.getUser().getName() + "||" + status.getText() + 
				"||" + status.getUser().getFavouritesCount() + "||" + status.getUser().getFollowersCount() + "||" + status.getUser().getFriendsCount() + 
				"||" + status.isFavorited() + "||" + status.isRetweet() +  "||" + status.getSource() +  "||" + status.getUser().getLocation());
        
		this.indexer.createIndex(status.getText(), status.getUser().getProfileImageURL());
		userNameTokenizer(status.getText());
		searchTermTokenizer(status.getText());
		urlTokenizer(status.getText());
		isInDictionaryTokenizer(status.getText());
		dao.addRecentTwitterer(status.getUser().getScreenName());
		dao.addTweetScore(status.getId(), calculateTweetScore(status.isFavorited(), status.isRetweet()));
		dao.addRecentTweet(status.getId(), status.getText());
	}

	private double calculateTweetScore(boolean isFavorited, boolean isRetweet) {
		double score = 0;
		if(isFavorited) {
			score += 1.0;
		}
		if(isRetweet) {
			score += 1.0;
		}
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

	private void searchTermTokenizer(String str) {
		String regex = "#[a-zA-Z0-9]+";
		TokenizerFactory tf = new RegExTokenizerFactory(regex);
		Tokenizer tokenizer = tf.tokenizer(str.toCharArray(), 0, str.length());
		Iterator<String> it = tokenizer.iterator();
		String keyword = null;
		while(it.hasNext()) {
			keyword = it.next();
			System.out.println("SEARCH_TERM>>>" + keyword);
			dao.addRecentSearchKeyword(keyword);
		}
	}
	
	private void urlTokenizer(String str) {
		String regex = "(http|https)://.+";
		TokenizerFactory tf = new RegExTokenizerFactory(regex);
		Tokenizer tokenizer = tf.tokenizer(str.toCharArray(), 0, str.length());
		Iterator<String> it = tokenizer.iterator();
		while(it.hasNext()) {
			System.out.println("URL>>>" + it.next());
		}
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
