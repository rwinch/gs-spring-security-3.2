package org.springframework.security.samples.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	@Order(1)
	@Configuration
	static class H2WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

		// @formatter:off
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.csrf().disable()
				.headers()
					.cacheControl()
					.contentTypeOptions()
					.httpStrictTransportSecurity()
					.and()
				.requestMatchers()
					.antMatchers("/h2/**")
					.and()
				.formLogin()
					.loginPage("/login")
					.and()
				.authorizeRequests()
					.anyRequest().hasRole("ADMIN");
		}
		// @formatter:on
	}

	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/resources/**","/signup").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}
	// @formatter:on

    // @formatter:off
	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.jdbcAuthentication()
				.dataSource(dataSource)
				.withDefaultSchema()
				.withUser("user").password("password").roles("USER").and()
				.withUser("admin").password("password").roles("USER","ADMIN");
	}
	// @formatter:on
}
