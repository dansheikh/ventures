package com.bcg.dv.configurations;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"com.bcg.dv.repositories"})
@EnableTransactionManagement
public class DatabaseConfiguration {
	
	@Bean
	@Profile("test")
	public DataSource dataSource() {
		EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
				.setName("esi")
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.ignoreFailedDrops(true)
				.addScripts("schema.sql", "data.sql")
				.build();

		return db;

		// return DataSourceBuilder.create().build();
	}
	
	@Bean
	@Profile("test")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.setPackagesToScan("com.bcg.dv.entities");
		factory.afterPropertiesSet();
		return factory;
	}
	
	@Bean
	@Profile("test")
	public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;		
	}
}
