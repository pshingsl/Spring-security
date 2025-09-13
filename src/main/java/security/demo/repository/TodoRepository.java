package security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.demo.entity.TodoEntity;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    List<TodoEntity> findByUserId(String userId);
}
// Entity 유저 아이디를 조회하기 위해서 사용 쿼리롤 select * from todo where userId = ?
