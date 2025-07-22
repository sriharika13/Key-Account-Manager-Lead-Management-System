package com.kamleads.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing //Enables automatic timestamp fields (@CreationTimestamp, @UpdateTimestamp)
@EnableTransactionManagement // Enables @Transactional annotation support
public class KamLeadManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamLeadManagementApplication.class, args);
	}

}
