package oleg.sichev.theideafactory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterSecurity(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests(authorize -> authorize
                        // Доступ к статическим ресурсам всем пользователям
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Доступ к странице логина и регистрации всем пользователям
                        .requestMatchers("/login", "/register").permitAll()
                        // Доступ на главную страницу и к постам для пользователей и админов
                        .requestMatchers("/", "/theIdeaFactoryIndex.html").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/posts/**").hasAnyRole("USER", "ADMIN")
                        // Запретить доступ к административным страницам и списку пользователей для пользователя role USER
                        .requestMatchers("/theIdeaFactoryIndexAdmin").hasRole("ADMIN")
                        .requestMatchers("/users").hasRole("ADMIN")
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Указываем страницу логина
                        .loginProcessingUrl("/login") // URL обработки логина
                        .defaultSuccessUrl("/", true) // URL после успешного логина
                        .permitAll() // Разрешаем доступ ко всем пользователям
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL для логаута
                        .permitAll() // Разрешаем доступ ко всем пользователям
                );

        return http.build();
    }
}
