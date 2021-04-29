package com.mycompany.appointmentservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, e) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()

                .antMatchers("/error").permitAll()

                .antMatchers(HttpMethod.POST, "/").authenticated()
                .antMatchers(HttpMethod.GET, "/find").authenticated()
                .antMatchers(HttpMethod.GET, "/patient/{patientUUID}").authenticated()
                .antMatchers(HttpMethod.GET, "/doctor/{doctorUUID}").authenticated()

                //access validated in services
                .antMatchers(HttpMethod.DELETE, "/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/{id}/details").authenticated()
                .antMatchers(HttpMethod.GET, "/{id}/details").authenticated()
                .antMatchers(HttpMethod.GET, "/{id}/details/prescription").authenticated()
                .antMatchers(HttpMethod.GET, "/{id}/details/attachment").authenticated()

                .anyRequest().denyAll();
    }
}
