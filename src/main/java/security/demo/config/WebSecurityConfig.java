package security.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import security.demo.security.JwtAuthenticationFilter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // 스프링 컨테이너에게 해당 클래스가 spring 설정 클래스임을 알림
@EnableWebSecurity // spring security 활성화
@Slf4j
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(withDefaults()) // cors 설정을 기본을호 설정
                .csrf(CsrfConfigurer::disable) // csrf() disable 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        // session 기반이 아니므로 무상태(STATELESS)로 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/api/auth/**")
                        // 요청경로가 "/", "/auth/**" 허용(permitAll)한다. -> 인증 안해도(제외) 된다.
                        .permitAll()
                        // 그 이외의 모든 경로는 인증해야한다.
                        .anyRequest().authenticated());

        httpSecurity.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return httpSecurity.build();
    }

    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 모든 출처, 메서드, 헤더에 대해 허용하는 cors설정
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders((Arrays.asList("*")));

        UrlBasedCorsConfigurationSource source  =  new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}


