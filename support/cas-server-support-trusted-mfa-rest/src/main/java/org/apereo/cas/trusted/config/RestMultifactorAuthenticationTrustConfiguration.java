package org.apereo.cas.trusted.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.support.CasFeatureModule;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecordKeyGenerator;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.authentication.storage.RestMultifactorAuthenticationTrustStorage;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.util.spring.boot.ConditionalOnFeature;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.client.RestTemplate;

/**
 * This is {@link RestMultifactorAuthenticationTrustConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration(value = "RestMultifactorAuthenticationTrustConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeature(feature = CasFeatureModule.FeatureCatalog.MultifactorAuthenticationTrustedDevices, module = "rest")
public class RestMultifactorAuthenticationTrustConfiguration {

    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @Bean
    public MultifactorAuthenticationTrustStorage mfaTrustEngine(
        final CasConfigurationProperties casProperties,
        @Qualifier("mfaTrustRecordKeyGenerator")
        final MultifactorAuthenticationTrustRecordKeyGenerator keyGenerationStrategy,
        @Qualifier("mfaTrustCipherExecutor")
        final CipherExecutor mfaTrustCipherExecutor) {
        return new RestMultifactorAuthenticationTrustStorage(casProperties.getAuthn()
            .getMfa()
            .getTrusted(), mfaTrustCipherExecutor, keyGenerationStrategy, new RestTemplate());
    }
}
