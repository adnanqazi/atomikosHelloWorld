package com.example.xademo;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class XaDemoApplication {

	private final XADataSourceWrapper wrapper;

	public XaDemoApplication(XADataSourceWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(XaDemoApplication.class, args);
	}



	@Bean
	@ConfigurationProperties(prefix="a")
	DataSource a() throws Exception {
		return wrapper.wrapDataSource(dataSource("a"));
	}

	@Bean
	@ConfigurationProperties(prefix="b")
	DataSource b() throws Exception {
		return wrapper.wrapDataSource(dataSource("b"));
	}

	@Bean
	DataSourceInitializer aInit(DataSource a){
		return  init(a,"a");
	}

	@Bean
	DataSourceInitializer bInit(DataSource b){
		return  init(b,"b");
	}

	private DataSourceInitializer init(DataSource ds, String name) {
		DataSourceInitializer dsi = new DataSourceInitializer();
		dsi.setDataSource(ds);
		dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(name+ ".sql")));
		return dsi;
	}

	private JdbcDataSource dataSource(String b) {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setUrl("jdbc:h2:mem:"+b);
		jdbcDataSource.setUser("sa");
		jdbcDataSource.setPassword("");
		return jdbcDataSource;
	}

	@org.springframework.web.bind.annotation.RestController
	public static class RestController{
		
		private final JdbcTemplate a, b;

		public RestController(DataSource a, DataSource b) {
			this.a = new JdbcTemplate(a);
			this.b =  new JdbcTemplate(b);
		}

		@GetMapping("/pets")
		public Collection<String> pets(){
			return (this.a.query("select * from PET", new RowMapper<String>(){

				@Override
				public String mapRow(ResultSet resultSet, int i) throws SQLException {
					return resultSet.getString("NICKNAME");
				}
			}));
		}

		@GetMapping("/messages")
		public Collection<String> messages(){
			return (this.b.query("select * from MESSAGE", new RowMapper<String>(){

				@Override
				public String mapRow(ResultSet resultSet, int i) throws SQLException {
					return resultSet.getString("MESSAGE");
				}
			}));
		}

		@PostMapping
		@Transactional
		public void write(@RequestBody Map<String, String> requestBody,
						  @RequestParam Optional<Boolean> rollback){
			String name = requestBody.get("name");
			String msg = "Hello " + name + " !";
			String id = UUID.randomUUID().toString();
			this.a.update("Insert into PET(id,nickname) values(?,?)", id, name);
			this.b.update("Insert into MESSAGE(id,message) values(?,?)", id, msg);

			if(rollback.orElse(false)) {
				throw new RuntimeException("ga ga lux");
			}
			
		}

	}

}
