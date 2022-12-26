package com.github.jfcloud.jos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
    RedisUtil的工具类
 */

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    // 判断是否存在key
    public boolean hasKey(final String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 删除key
    public void del(final String key){
        if (hasKey(key)){
            redisTemplate.delete(key);
        }
    }

    // ============================================ list ============================================
    // 存入列表
    public boolean setList(final String key, Object value, Integer expireTime){
        try {
            redisTemplate.opsForList().rightPush(key,value);
            redisTemplate.expire(key,expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取列表
    public List<Object> getList(final String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 删除列表中的元素
    public void removeValue(final String key,String value){
        redisTemplate.opsForList().remove(key,1,value);
    }

}
