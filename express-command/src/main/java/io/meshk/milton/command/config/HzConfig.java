package io.meshk.milton.command.config;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;

public class HzConfig {

    @Bean
    Config config() {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", "default")
                .setProperty("service-name", "hazelcast-service");
        return config;
    }

}
