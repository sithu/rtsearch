/**
 * 
 */
package com.rtsearch.indexing;

import java.net.URL;

/**
 * @author saung
 *
 */
public interface Indexer {
	public void createIndex(String content, URL profileImageUrl, float weight);
	public void closeIndex();
}
