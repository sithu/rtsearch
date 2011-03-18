package com.rtsearch.dao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 * @author saung
 *
 */
public class RedisDataStore {
	private static final JedisPool pool = new JedisPool(new Config(), "localhost");
	
	/**
	 * 
	 */
	public RedisDataStore() {
		
	}
	
	public Jedis getConnection() {
		return pool.getResource();
	}

	public void set(String key, String value) {
		Jedis jedis = getConnection();
		jedis.set(key, value);
		pool.returnResource(jedis);
	}
	
	public void addSet(String key, String member) {
		Jedis jedis = getConnection();
		jedis.sadd(key, member);
		pool.returnResource(jedis);
	}
	
	public Long addSortedSet(String key, double score, String member) {
		Jedis jedis = getConnection();
		Long result = jedis.zadd(key, score, member);
		pool.returnResource(jedis);
		return result;
	}
}
