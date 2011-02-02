/**
 * 
 */
package com.rtsearch.twitter;

import com.rtsearch.indexing.Indexer;

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
	
	/**
	 * @param indexer
	 */
	public TwitterStatusListener(Indexer indexer) {
		super();
		this.indexer = indexer;
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
		System.out.println(status.getUser().getId() + " : " + 
				(status.getUser().isGeoEnabled() ? status.getGeoLocation() : "NO_GEO") + " : " + status.getUser().getName() + " : " + status.getText());

		this.indexer.createIndex(status.getText());
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub

	}

}
