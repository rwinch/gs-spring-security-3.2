package org.springframework.security.samples.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.samples.security.UserRepositoryUserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses=UserRepositoryUserDetailsService.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;

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
			.userDetailsService(userDetailsService);
	}
	// @formatter:on
}
