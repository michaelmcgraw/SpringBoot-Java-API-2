package com.kenzie.appserver.cache;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class CacheClient {

    private final JedisPool jedisPool;

    public CacheClient() {
        this.jedisPool = new JedisPool(
                new JedisPoolConfig(),
                "localhost",
                6379,
                20000);;
    }
    public void setValue(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }
    public String getValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }
    public void deleteValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}