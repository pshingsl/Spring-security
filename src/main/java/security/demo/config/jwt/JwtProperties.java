package security.demo.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String issuer;
    // application.properties JWT주석 아래에 jwt.issuer
    private String secretKey;
    // application.properties jwt.secret_key 가져옴  secret_key =  private String secretKey; 같음
}

// application.properties에 파일에 있는 설정 값을 가졍고자 하는 클래스