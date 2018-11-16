package com.ice.seed.slidingvalidation.Controller;

import net.sf.ehcache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : IceSeed
 * @version : v0.0.1
 * @since : 2018/11/16
 */
@Configuration
public class EhcacheConfig {
    @Bean
    public CacheManager getCacheManager(){
        return new CacheManager();
    }
}
