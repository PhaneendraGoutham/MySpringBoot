package com.forsrc.boot.config;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Autowired
    private Environment env;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource);

        entityManagerFactory.setPackagesToScan(
                env.getProperty("entitymanager.packagesToScan"));

        String persistenceXmlLocation = env.getProperty("entitymanager.persistenceXmlLocation");
        if (persistenceXmlLocation != null) {
            entityManagerFactory.setPersistenceXmlLocation(persistenceXmlLocation);
        }

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.put(
                "hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        properties.put(
                "hibernate.show_sql",
                env.getProperty("hibernate.show_sql"));
        properties.put(
                "hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));

        properties.put(
                "hibernate.format_sql",
                env.getProperty("hibernate.format_sql"));
        String mappingResources = env.getProperty("hibernate.mappingResources");
        if (mappingResources != null) {
            //entityManagerFactory.setMappingResources(DatabaseConfig.class.getResource(mappingResources).toString());
        }
        entityManagerFactory.setJpaProperties(properties);
        return entityManagerFactory;
    }

    /*
     @Bean
     public LocalSessionFactoryBean sessionFactory() throws IOException {
     LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
     String mappingResources = env.getProperty("hibernate.mappingResources");
     Properties properties = new Properties();
     properties.put(
     "hibernate.dialect",
     env.getProperty("hibernate.dialect"));
     properties.put(
     "hibernate.show_sql",
     env.getProperty("hibernate.show_sql"));
     properties.put(
     "hibernate.hbm2ddl.auto",
     env.getProperty("hibernate.hbm2ddl.auto"));

     properties.put(
     "hibernate.format_sql",
     env.getProperty("hibernate.format_sql"));
     if (mappingResources != null) {
     
     }

     sessionFactoryBean.setHibernateProperties(properties);
     return sessionFactoryBean;
     }
     */
    /*
    @Bean(name = "jpaTransactionManager", autowire = Autowire.BY_NAME)
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager =
                new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory.getObject());
        return transactionManager;
    }


    @Bean(name = "txManager02", autowire = Autowire.BY_NAME)
    @Qualifier(value = "txManager02")
    public PlatformTransactionManager txManager01() {
        return new DataSourceTransactionManager(dataSource);
    }
    */
    @Bean(name = "txManager01", autowire = Autowire.BY_NAME)
    @Qualifier(value = "txManager01")
    public PlatformTransactionManager txManager02() {
        JpaTransactionManager transactionManager =
                new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}