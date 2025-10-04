package in.neelesh.auth_proxy.config;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfiniSpanConfig {

	@Bean(destroyMethod = "stop")
	public RemoteCacheManager remoteCacheManager() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.marshaller(JavaSerializationMarshaller.class);
		builder.addServer()
				.host("localhost")
				.port(11222)
				.security()
				.authentication()
				.username("admin")
				.password("admin")
				.addJavaSerialAllowList(".*");
		builder.clientIntelligence(org.infinispan.client.hotrod.configuration.ClientIntelligence.BASIC);
		return new RemoteCacheManager(builder.build());
	}

	/**
	 * Define the otp-cache explicitly as a Spring bean
	 */
	@Bean
	public RemoteCache<String, String> otpCache(RemoteCacheManager remoteCacheManager) {
		RemoteCache<String, String> cache = remoteCacheManager.getCache("otp-cache");
		if (cache == null) {
			throw new IllegalStateException("Cache 'otp-cache' is not defined on the Infinispan server.");
		}
		return cache;
	}
}