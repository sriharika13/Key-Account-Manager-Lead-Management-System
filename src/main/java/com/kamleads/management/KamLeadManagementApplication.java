package com.kamleads.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing //Enables automatic timestamp fields (@CreationTimestamp, @UpdateTimestamp)
@EnableTransactionManagement // Enables @Transactional annotation support
@EnableJpaRepositories(
		basePackages = "com.kamleads.management.repository", // Explicitly define where repositories are
		queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND
)
@EntityScan(basePackages = "com.kamleads.management.model")
public class KamLeadManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamLeadManagementApplication.class, args);
	}

}
