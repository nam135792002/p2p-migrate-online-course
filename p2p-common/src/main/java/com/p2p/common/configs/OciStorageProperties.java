package com.p2p.common.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds the OCI Object Storage configuration properties.
 *
 * @param region OCI region identifier
 * @param namespace OCI Object Storage namespace
 * @param bucket OCI Object Storage bucket name
 * @param configFile path to the OCI configuration file
 * @param profile OCI configuration profile name
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-19
 */
@ConfigurationProperties(prefix = "oci.object-storage")
public record OciStorageProperties(
    String region, String namespace, String bucket, String configFile, String profile) {}
