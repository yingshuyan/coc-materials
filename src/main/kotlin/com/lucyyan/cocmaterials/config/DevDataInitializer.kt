package com.lucyyan.cocmaterials.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
@Profile("dev")
class DevDataInitializer {

    @Bean
    fun devDataLoader(
        dataSource: DataSource
    ): ApplicationRunner {
        return ApplicationRunner {
            ResourceDatabasePopulator(
                ClassPathResource("dev/seed-data.sql")
            ).execute(dataSource)
        }
    }
}