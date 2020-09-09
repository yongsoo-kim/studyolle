package com.studyolle.studyolle.infra.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.mvcMatchers("/", "/login", "/sign-up", "/check-email-token", "/email-login", "/login-by-email",
						"/check-email-login", "/login-link").permitAll()
				.mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
				.anyRequest().authenticated();
		
		http.formLogin()
			// Default is "username" and "password"
			//.usernameParameter("customPara")
			//.passwordParameter("customPara")
			.loginPage("/login").permitAll();
		
		http.logout()
			.logoutSuccessUrl("/");
		
		http.rememberMe()
			//.key("hashbasedcookie - not safe")
			.userDetailsService(userDetailsService)
		    .tokenRepository(tokenRepository());
		
	}
	
	@Bean 
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}


	@Override
	public void configure(WebSecurity web) throws Exception {
		// for static files
		web.ignoring()
				.mvcMatchers("/node_modules/**") // for ignoring 'node_module'
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // for ignoring common 'static' files from security setting.

	}
	
	

}
