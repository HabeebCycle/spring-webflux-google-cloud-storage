package com.habeebcycle.gcpstorage.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    public Storage getStorage() {
        return StorageOptions
                .getDefaultInstance()
                .getService();
    }
}
