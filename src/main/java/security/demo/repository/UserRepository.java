package security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.demo.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    Boolean existsByEmail(String email); // 이메일 존재 여부 있음(참), 없음(거짓) -> 기본형으로 처리 안되어서 참조형타입 처러
    UserEntity findByEmailAndPassword(String email, String password);
}
