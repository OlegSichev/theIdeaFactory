package oleg.sichev.theideafactory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

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
                        // Доступ на главную страницу и к постам для пользователей и администраторов
                        .requestMatchers("/", "/theIdeaFactoryIndex.html").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/posts/**").hasAnyRole("USER", "ADMIN")
                        // Доступ к административным страницам разрешён только для администраторов
                        .requestMatchers("/theIdeaFactoryIndexAdmin").hasRole("ADMIN")
                        // Доступ к списку пользователей разрешён только для администраторов
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/add_user").hasRole("ADMIN")
                        .requestMatchers("/edit_user/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/login") // Указываем страницу логина
                        .loginProcessingUrl("/login") // URL обработки логина
                        .successHandler(customSuccessHandler()) // Указываем кастомный обработчик успешного логина
                        .permitAll() // Разрешаем доступ ко всем пользователям
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL для логаута
                        .permitAll() // Разрешаем доступ ко всем пользователям
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                String redirectUrl = request.getContextPath();
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        redirectUrl += "/theIdeaFactoryIndexAdmin"; // Главная страница для администраторов
                        break;
                    }
                }

                if (redirectUrl.equals(request.getContextPath())) {
                    redirectUrl += "/theIdeaFactoryIndex"; // Главная страница для обычных пользователей
                }

                response.sendRedirect(redirectUrl);
            }
        };
    }
}