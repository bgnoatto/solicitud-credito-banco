package com.carsa.credito.banco.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
public class DataBaseMSConfig implements EnvironmentAware 
{
	private RelaxedPropertyResolver dataSourcePropertyResolver;

    @Override
    public void setEnvironment(Environment env) {
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(env, "spring.ms.datasource.");
    }   
    
	@Bean(name="jdbcTemplateMs")
	public JdbcTemplate jdbcTemplateMs(DataSource msDataSource){
	     return  new JdbcTemplate(msDataSource);
	}
	
	@Bean(destroyMethod = "close")
	@ConfigurationProperties(prefix = "ms.datasource")
    public DataSource msDataSource() throws SQLException {
		HikariConfig hikariConfig = new HikariConfig();
	    
	    hikariConfig.setDriverClassName(dataSourcePropertyResolver.getProperty("driverClassName"));
	    hikariConfig.setJdbcUrl(dataSourcePropertyResolver.getProperty("url")); 
	    hikariConfig.setUsername(dataSourcePropertyResolver.getProperty("username"));
	    hikariConfig.setPassword(dataSourcePropertyResolver.getProperty("password"));
	    hikariConfig.setMinimumIdle(Integer.parseInt(dataSourcePropertyResolver.getProperty("minimumIdle")));
	    hikariConfig.setMaximumPoolSize(Integer.parseInt(dataSourcePropertyResolver.getProperty("maximumPoolSize")));
	    hikariConfig.setConnectionTestQuery(dataSourcePropertyResolver.getProperty("connectionTestQuery"));
	    hikariConfig.setPoolName(dataSourcePropertyResolver.getProperty("poolName"));
	    hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", dataSourcePropertyResolver.getProperty("cachePrepStmts"));
	    hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", dataSourcePropertyResolver.getProperty("prepStmtCacheSize"));
	    hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit",dataSourcePropertyResolver.getProperty("prepStmtCacheSqlLimit"));
	    hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", dataSourcePropertyResolver.getProperty("useServerPrepStmts"));
	    
        return new HikariDataSource(hikariConfig);
    }
	

	
}