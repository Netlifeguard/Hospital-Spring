package com.nie.common.config;

import com.nie.common.tools.MyUserDetailsService;
import com.nie.feign.client.DoctorClient;
import com.nie.feign.client.PatientClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/auth/captcha", "/patient/securityP", "/doctor/securityD").permitAll()  // 放行验证码接口,登录接口
                .antMatchers("/admin/login", "/admin/logout", "/doctor/login", "/doctor/logout", "/patient/login", "/patient/logout").permitAll()  // 放行这些路径
                .anyRequest().authenticated()  // 其他请求需要认证
                //.anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")  // 自定义登录页面
                .permitAll()  // 允许所有用户访问登录页面
                .and()
                .logout()
                .permitAll()  // 允许所有用户访问注销页面
                .and()
                .csrf().disable();  // 根据需要禁用 CSRF

        return http.build();
    }


    @Bean
    UserDetailsService userDetailsService() {
        return new MyUserDetailsService(patientClient, doctorClient, bCryptPasswordEncoder());
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())  // 关联 DaoAuthenticationProvider
                .build();
    }
}
