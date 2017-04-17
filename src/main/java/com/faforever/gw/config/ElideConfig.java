package com.faforever.gw.config;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.audit.Slf4jLogger;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.datastores.hibernate5.HibernateStore;
import com.yahoo.elide.security.checks.Check;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ElideConfig {
    @Bean
    public Elide elide(EntityManagerFactory entityManagerFactory) {
        ConcurrentHashMap<String, Class<? extends Check>> checks = new ConcurrentHashMap<>();
//        checks.put("user is a superuser", AdminCheck.class);

        EntityDictionary entityDictionary = new EntityDictionary(checks);
        RSQLFilterDialect rsqlFilterDialect = new RSQLFilterDialect(entityDictionary);


        HibernateStore hibernateStore = new HibernateStore.Builder(entityManagerFactory.unwrap(SessionFactory.class)).build();

        return new Elide(new ElideSettingsBuilder(hibernateStore)
                .withAuditLogger(new Slf4jLogger())
                .withEntityDictionary(entityDictionary)
                .withJoinFilterDialect(rsqlFilterDialect)
                .withSubqueryFilterDialect(rsqlFilterDialect)
                .build());
    }
}
