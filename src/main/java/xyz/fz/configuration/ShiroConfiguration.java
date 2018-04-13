package xyz.fz.configuration;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfiguration {

    @Bean
    public Realm realm() {
        // uses 'classpath:shiro-users.properties' by default
        PropertiesRealm realm = new PropertiesRealm();
        // Caching isn't needed in this example, but we can still turn it on
        realm.setCachingEnabled(true);
        return realm;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/doLogin", "anon");
        chainDefinition.addPathDefinition("/doLogout", "authc");
        chainDefinition.addPathDefinition("/pubs/**", "anon");
        chainDefinition.addPathDefinition("/h2console/**", "authc");
        chainDefinition.addPathDefinition("/", "authc");
        chainDefinition.addPathDefinition("/home/**", "authc");
        chainDefinition.addPathDefinition("/http/**", "authc");
        chainDefinition.addPathDefinition("/mail/**", "authc");
        chainDefinition.addPathDefinition("/**", "authc, perms[no]");
        return chainDefinition;
    }

    @Bean
    protected CacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }
}
