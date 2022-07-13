package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity //security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired
	private CorsConfig corsConfig;

	/**
	 * Spring Security 5.7.x 부터 WebSecurityConfigurerAdapter 는 Deprecated. ->
	 * SecurityFilterChain, WebSecurityCustomizer 를 상황에 따라 빈으로 등록해 사용한다.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.addFilter(corsConfig.corsFilter()).csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.formLogin().disable()
				/*form 태그 로그인 x -> 세션 메모리 문제 ?
				 * 			     -> ajax 사용시 javascript를 통해 id와 pw를 서버에 요청하게 됨. javascript로 요청할 경우, 클라이언트가 갖고 있는 쿠키를 전송하지 못함. 대부분의 서버는 httpOnly값이 true로 설정
									httpOnly 값을 false로 설정하면 요청이 오기는 오지만, 보안상 좋지 않음*/
				.httpBasic().disable() // httpBasic대신 bearer 방식을 사용

//				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
//				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
				.authorizeRequests()
				.antMatchers("demo/user/**")
				.access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
				.antMatchers("demo/admin/**").access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll();

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().mvcMatchers("/node_modules/**")
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}