package com.sysmap.srcmssignportability.framework.adapters.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    public @Bean MongoClient mongoClient() {
        return MongoClients.create("mongodb+srv://sysmap_ms:Entrar0123@sysmap-portability.kurcw.mongodb.net/db-ms-sign-portability?retryWrites=true&w=majority");
    }
}

