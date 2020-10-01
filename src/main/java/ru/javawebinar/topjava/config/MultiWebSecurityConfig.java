package ru.javawebinar.topjava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.service.UserService;

@EnableWebSecurity
public class MultiWebSecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(delegatingPasswordEncoder());
    }

    @Order(1)
    @Configuration
    public static class RestSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http/*.requiresChannel().anyRequest().requiresSecure().and()*/
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().antMatcher("/rest/**")
                    .authorizeRequests()
                    .antMatchers("/rest/admin/**").hasAuthority(Role.ADMIN.getAuthority())
                    .antMatchers("/rest/profile/register").anonymous()
                    .antMatchers("/rest/**").authenticated()
                    .and().httpBasic()
                    .and().csrf().disable();
        }
    }

    @Order(2)
    @Configuration
    public static class FormLoginSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                    .antMatchers("/webjars/**", "/resources/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http/*.requiresChannel().anyRequest().requiresSecure().and()*/
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasAuthority(Role.ADMIN.getAuthority())
                    .antMatchers("/profile/register").anonymous()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/**").authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/spring_security_check")
                    .defaultSuccessUrl("/meals", true)
                    .failureUrl("/login?error=true")
                    .and()
                    .logout()
                    .permitAll()
                    .logoutSuccessUrl("/login");
        }
    }
}
