package com.rtsearch.util;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author saung
 *
 */
public class LucenceUtil {
	/**
	 * 
	 * @param dirPath
	 * @return
	 * @throws IOException
	 */
	public static Directory getIndexDir(String dirPath) throws IOException {
		return FSDirectory.open(new File(dirPath));
	}
}
