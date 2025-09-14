package security.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.demo.config.jwt.JwtProperties;
import security.demo.entity.UserEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/*
 * 사용자 정보를 받아 JWT 생성
 * 서비스를 사용하는 이유: 서비스 계층 컴포넌트롤 등록해서 다른곳에 주입받아서 쓰이기 위해서이다.
 */
@Service
@Slf4j
public class TokenProvider {
    /*
     * JWT 서명에 사용되는 비밀키로 지금은 하드 코딩함
     * private static final String SECRET_KEY = "4291";
     * [after] JwtProperties 적용
     */
    @Autowired
    private JwtProperties jwtProperties;

    // create(): JWT 생성
    // 로그인 성공 시에 이 메서드가 호출되어 JWT 토큰을 발급한다.
    public String create(UserEntity entity) {
        // JWT 토큰 만료일 설정: 생성된 시간으로부터 24시간동안 유지
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        // JWT 빌더를 사용해 토큰 생성
        return Jwts.builder()
                /*
                 * JWT 구조인(Header, Payload, Signature)
                 * header에 들어갈 내용 및 서명하기 위한 SECRET_KEY
                 * .signWith(SignatureAlgorithm.HS512, jSECRET_KEY)
                 */
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                // payload 구역
                .setSubject(String.valueOf(entity.getId())) // sub: 토큰 제목(여기서는 userId)
                .setIssuer("demo app") // iss: 토큰 발급자(임의 이름 demo app 지정)
                .setIssuedAt(new Date()) //ist: 토큰이 발급된 시간
                .setExpiration(expiryDate) // exp: 토큰 만료시간
                .compact(); // 토큰생성 -> "header.payload.signature" 토큰 문자열 최종 생성
    }

    /*
     * validateAndGetUserId(): 토큰 디코디 및 파싱하고 토큰 위조 여부를 확인
     * -> 사용자 아이디 리턴
     *   아래 메서드는 클라이언트가 보낸 토큰이 유효한지 검증하고, userId를 변환
     *
     *  parseClaimsJws 메서드가 Base64로 디코딩 및 파싱
     * - header, payload를 setSignKey로 넘어온 시크릿을 이용해 서명한 후 token의 서명이랑 비교
     * - 서명이 위조되거나 만료된 토큰이라면 예외 발생
     * - 위조되지 않았다면 payload(claims) 리턴
     */
    public String validateAndGetUserId(String token) {
        Claims claims = Jwts.parser()
                // .setSigningKey(SECRET_KEY) 서명검증에 사용할 비밀키 지정
                .setSigningKey(jwtProperties.getSecretKey()) // jwt 파일 적용
                .parseClaimsJws(token) // jwt 파싱 ->  header, payload, sihnature 검증
                .getBody();

        return claims.getSubject(); // jwt 생성시 넣었던 sub(userId) 값을 꺼냄
    }
}


