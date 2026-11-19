package com.p2p.common.configs;

import java.io.IOException;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

/**
 * Creates the shared OCI Object Storage client bean.
 *
 * <p>The client is configured from {@link OciStorageProperties} and is closed by Spring when the
 * application context shuts down.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-19
 */
@Configuration
@EnableConfigurationProperties(OciStorageProperties.class)
public class OciStorageConfig {

    /**
     * Creates an OCI Object Storage client.
     *
     * @param properties OCI Object Storage configuration properties
     * @return the configured Object Storage client
     * @throws IOException if the OCI configuration file cannot be parsed
     */
    @Bean(destroyMethod = "close")
    public ObjectStorage objectStorage(OciStorageProperties properties) throws IOException {
        ConfigFile configFile = ConfigFileReader.parse(properties.configFile(), properties.profile());
        AuthenticationDetailsProvider detailsProvider = new ConfigFileAuthenticationDetailsProvider(configFile);

        return ObjectStorageClient.builder().region(Region.fromRegionId(properties.region())).build(detailsProvider);
    }
}
