package com.faforever.gw.config;

import com.faforever.gw.security.GetRequestTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final GetRequestTokenAuthenticationFilter getRequestTokenAuthenticationFilter;

    @Inject
    public WebSecurityConfig(GetRequestTokenAuthenticationFilter getRequestTokenAuthenticationFilter) {
        this.getRequestTokenAuthenticationFilter = getRequestTokenAuthenticationFilter;
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
//                .addFilterBefore(getRequestTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().disable().and()
                .csrf().disable()
//                .formLogin()
//				.loginPage("/index.html")
//				.defaultSuccessUrl("/chat.html")
//				.permitAll()
//				.and()
//			.logout()
//				.logoutSuccessUrl("/index.html")
//				.permitAll()
//				.and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/js/**", "/lib/**", "/images/**", "/css/**", "/index.html", "/").permitAll()
//				.antMatchers("/websocket").permitAll()//.hasRole("ADMIN")
                .antMatchers("/data/**").permitAll()
                .anyRequest().authenticated();

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("*");
			}
		};
	}
}
