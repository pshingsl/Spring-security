package security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import security.demo.dto.ResponseDTO;
import security.demo.dto.UserDTO;
import security.demo.entity.UserEntity;
import security.demo.security.TokenProvider;
import security.demo.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService service;

    // [after] jwt 적용
    @Autowired
    private TokenProvider tokenProvider;

    // [aftre] 패스워드 암호화 적용
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> create(@RequestBody UserDTO dto) {
        try {
            // 요청 본문을 이용해 저장할 사용자 생성
            UserEntity user = UserEntity.builder()
                    .email(dto.getEmail())
                    // .password(dto.getPassword())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .username(dto.getUsername())
                    .build();

            // 서비스 계층 메서드를 이용해 repository에 저장
            UserEntity registeredUser = service.create(user);
            UserDTO response = UserDTO.builder()
                    .email(registeredUser.getEmail())
                    .password(registeredUser.getPassword())
                    .username(registeredUser.getUsername())
                    .build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO dto) {
        UserEntity user = service.getByCredentials(
                dto.getEmail(), dto.getPassword(), passwordEncoder
        ); // 시큐리티가 제공하는 getByCredentials() 이용해 이메일, 비민번호를 변수 user에 담음

        if (user != null) {
            // 로그인 검사 성공

            /*
             * jwt 적용전
             *   final UserDTO response = UserDTO.builder()
             *      .email(user.getEmail())
             *      .id(user.getId())
             *      .build()
             */

            // jwt 적용 후
            final String token = tokenProvider.create(user);
            final UserDTO response = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(response);
        } else {
            // 로그인 실패
            ResponseDTO response = ResponseDTO.builder()
                    .error("로그인 실패!")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
