package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringSecureApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecureApplication.class, args);
	}

}

@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	UserDetailsService authentication() {
		
		final PasswordEncoder pwEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		UserDetails marcos = User.builder()
				.username("marcos")
				.password(pwEncoder.encode("password"))
				.roles("USER")
				.build();
		
		UserDetails laurie = User.builder()
				.username("laurie")
				.password(pwEncoder.encode("StrongPassword!"))
				.roles("USER", "ADMIN")
				.build();
		
		System.out.println("Marco's password: " + marcos.getPassword());
		System.out.println("Laurie's password: " + laurie.getPassword());
		
		return new InMemoryUserDetailsManager(marcos, laurie);
				
	}
	
	@Override // configures authorization
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().mvcMatchers("/admin/**").hasRole("ADMIN").and()
		.authorizeRequests().anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.httpBasic();
		
	}
}

@RestController
class FormController {
	@GetMapping("/everyone")
	String getEveryone() {
		return "Hola a todos";
	}
	
	@GetMapping("/admin")
	String adminOnly() {
		return "<h1>Página de administrador</h1>Saludos Admin!"; 
	}
}
