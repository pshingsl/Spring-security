package security.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import security.demo.entity.UserEntity;
import security.demo.repository.UserRepository;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public UserEntity create(UserEntity entity) {
        // 유효성 검사 1) userEntity 혹은 email이 null인 경우 에외 던짐
        if (entity == null || entity.getEmail() == null) {
            throw new RuntimeException("Invalid arguments");
        }

        // 유효성 검사 2) 이메일이 이미 존재하는 경우 예외를 던짐(email 필드는 유니크 해야 하므로)
        String email = entity.getEmail();
        if (repository.existsByEmail(email)) {
            log.warn("Email already exist {}", email);
            throw new RuntimeException("Email already exist");
        }

        return repository.save(entity);
    }
    // 인증: 이메일과 비밀번호로 사용자 조회
//    public UserEntity getByCredentials(final String email, final String password){
//     DB에서 해당 email, password가 일치라는 유저가 있는지를 조회
//     return userRepository.findByEmailAndPassword(email, password);
//    }

    // 패스워드 암호화 적용 후
    public UserEntity getByCredentials(final String email, final String password, PasswordEncoder encoder) {
        UserEntity originalUser = repository.findByEmail(email);

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            /*
             * password: 클라이언트가 줒아하는 현재 유저에 대한 비밀번호
             * originalUser.getPassword(): DB에 저장된 정답 비밀번호
             * 두개를 비교한다?
             */
            return originalUser; // 인증 성공 -> DB에 저장된 유저 리턴
        }
        return null; // 인증 실패 → 해당 이메일 없거나 비밀번호 틀림
    }
}
/*
 * matches: salt를 고려해 두 값을 비교하는 메소드
 * PasswordEncoder: security에서 제공하는 기능이며 비밀번호 암호화하는데 쓰임
 * 조건이 만족하면 originalUser리턴
 * 그렇지 않으면  return null;
 * */
