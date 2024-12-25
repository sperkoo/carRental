package com.cars.cars;

import com.cars.cars.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Use NoOpPasswordEncoder for plain text passwords
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/cars").hasAnyRole("USER", "ADMIN")
                .antMatchers("/orders/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/bookingform/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/add-vehicle").hasRole("ADMIN")
                .antMatchers("/new-customer/**").hasRole("ADMIN")
                .antMatchers("/update-booking/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/update-car/**").hasRole("ADMIN")
                .antMatchers("/update-customer/**").hasRole("ADMIN")
                .antMatchers("/error-delete").hasRole("ADMIN")
                .antMatchers("/customers/**").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/admin/vehicles").hasRole("ADMIN")
                .antMatchers("/admin/customers").hasRole("ADMIN")
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error");
    }
}