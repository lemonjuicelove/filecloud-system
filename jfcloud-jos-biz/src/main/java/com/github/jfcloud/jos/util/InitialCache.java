package com.github.jfcloud.jos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
    加载Redis中的数据到本地缓存
 */
@Component
@Order(1)
public class InitialCache implements ApplicationRunner {

    private static Map<Object, Object> cacheMap = new HashMap<>();

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public static Map<Object, Object> getCacheMap() {
        return cacheMap;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<Object, Object> storageType = redisTemplate.opsForHash().entries("storageType");
        for (Map.Entry<Object, Object> entry : storageType.entrySet()) {
            cacheMap.put(entry.getKey(),entry.getValue());
        }
    }

}
