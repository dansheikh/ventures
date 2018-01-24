package com.bcg.dv.configurations;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
	
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2Ddl;
	
	@Value("${hibernate.show_sql}")
	private String hibernateShowSql;
	
	@Bean
	@Profile("test")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setName("esi")
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.ignoreFailedDrops(true)
				.addDefaultScripts()
				.build();

		// return DataSourceBuilder.create().build();
	}
	
	@Bean
	@Profile("test")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		final Properties jpaVendorProperties = new Properties();
		
		jpaVendorProperties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2Ddl);
		jpaVendorProperties.setProperty("hibernate.dialect", hibernateDialect);
		jpaVendorProperties.setProperty("hibernate.show_sql", hibernateShowSql);
		
		jpaVendorAdapter.setGenerateDdl(false);

		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setJpaProperties(jpaVendorProperties);
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
