package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.support.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeature;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

import java.time.Duration;

/**
 * This is {@link MongoSessionConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration(value = "MongoSessionConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ImportAutoConfiguration({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableMongoHttpSession
@ConditionalOnFeature(feature = CasFeatureModule.FeatureCatalog.SessionManagement, module = "mongo")
public class MongoSessionConfiguration {
    private static final int DURATION_MINUTES = 15;

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public JdkMongoSessionConverter jdkMongoSessionConverter() {
        return new JdkMongoSessionConverter(Duration.ofMinutes(DURATION_MINUTES));
    }

    @Bean
    @Primary
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public MongoOperations mongoTemplate(final MongoDatabaseFactory factory,
                                         final MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }
}
