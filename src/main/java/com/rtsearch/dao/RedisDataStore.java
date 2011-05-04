package com.rtsearch.dao;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 * This class implements low level CRUD Redis operations.
 * 
 * @author saung
 * @since v1.0
 */
public class RedisDataStore {
	/**
	 * a connection pool to share between clients.
	 */
	private static final JedisPool pool = new JedisPool(new Config(), "localhost");
	
	/**
	 * Default Constructor
	 */
	public RedisDataStore() {
	}
	
	/**
	 * Get a connection from the pool.
	 * @return a connection.
	 */
	public Jedis getConnection() {
		return pool.getResource();
	}

	/**
	 * Set a new value to a key.
	 * @param key - an unique key.
	 * @param value - a new value to be set.
	 */
	public void set(String key, String value) {
		Jedis jedis = getConnection();
		jedis.set(key, value);
		pool.returnResource(jedis);
	}
	
	/**
	 * Add a new member to the set.
	 * @param key - a key.
	 * @param member - a new member to add to the given set.
	 */
	public void addSet(String key, String member) {
		Jedis jedis = getConnection();
		jedis.sadd(key, member);
		pool.returnResource(jedis);
	}
	
	/**
	 * Add a new member to a sorted set.
	 * @param key - a key to the set.
	 * @param score - a score for the member
	 * @param member - a member.
	 * @return new index of the member
	 */
	public Long addSortedSet(String key, double score, String member) {
		Jedis jedis = getConnection();
		Long result = jedis.zadd(key, score, member);
		pool.returnResource(jedis);
		return result;
	}
	
	/**
	 * Increment sorted set by a given score.
	 * @param key - a key to the set.
	 * @param score - a score to be added for the member.
	 * @param member - a member
	 * @return new score
	 */
	public Double incrementSortedSet(String key, double score, String member) {
		Jedis jedis = getConnection();
		Double result = jedis.zincrby(key, score, member);
		pool.returnResource(jedis);
		return result;
	}
	
	/**
	 * Get sorted set by score.
	 * 
	 * @param key - a key
	 * @param min - a minimum score.
	 * @param max - a maximum score.
	 * @param offset - an offset to set between members. 
	 * @param count - a count to be returned fromt the list.
	 * @return a set of members within the given range of min and max. 
	 */
	public Set<String> getSortedSetByScore(String key, double min, double max, int offset, int count) {
		Jedis jedis = getConnection();
		final Set<String> result = jedis.zrangeByScore(key, min, max, offset, count);
		pool.returnResource(jedis);
		return result;
	}
	
}
