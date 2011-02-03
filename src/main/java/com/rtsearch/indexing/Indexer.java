/**
 * 
 */
package com.rtsearch.indexing;

/**
 * @author saung
 *
 */
public interface Indexer {
	public void createIndex(String content);
	public void closeIndex();
}
