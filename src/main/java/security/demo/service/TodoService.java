package security.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.demo.dto.TodoDTO;
import security.demo.entity.TodoEntity;
import security.demo.repository.TodoRepository;

import java.util.List;

@Slf4j
@Service
public class TodoService {
    @Autowired
    private TodoRepository repository;

    // create
    public List<TodoEntity> create(TodoEntity entity) {
        validate(entity);

        repository.save(entity);

        log.info("Entity Id: {} is saved", entity.getId()); // 로그 찍기

        return repository.findByUserId(entity.getUserId());
    }

    // read
    public  List<TodoEntity> retrieve(String userId) {
        return repository.findByUserId(userId);
    }

    // update
    public List<TodoEntity> update(long id, String userId,  TodoEntity entity) {
        TodoEntity updateEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo Not Found"));

        if(!updateEntity.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        updateEntity.setTitle(entity.getTitle());
        updateEntity.setDone(entity.isDone());

        repository.save(updateEntity);

        return repository.findByUserId(userId);

//        repository.save(entity);
//
//        return repository.findByUserId(userId);
    }

    // delete
    public void delete(long id, String userId) {
        TodoEntity delete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo Not Found"));

        if(!delete.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        repository.deleteById(id);
    }


    private void validate(TodoEntity entity) {
        if(entity == null){
            log.warn("Entity는 null을 사용할 수 없다.");
            throw new RuntimeException("Entity는 null을 사용할 수 없다.");
        }

        if(entity.getUserId() == null){
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user");
        }
    }
}

/*
 * 유효성 검사
 * - 데이터의 무결성과 안정성을 확보하기 위해서이다.
 * - 단순히 회원가입뿐만 아니라, 데이터베이스에 새로운 데이터를 저장하거나 기존 데이터를 수정할 때 항상 필요
 * - TodoEntity가 데이터베이스의 저장되기 전에 두가지 사항 확인
 *
 * 1. entity가 null인지 확인
 * - 클라이언트로부터 받은 데이터가 유효하지 않을 수 있다.
 * - 잘못된 형식 JSON을 보내거나, HTTP요청 본문이 비어있을경우 TodoEntity 객체가 null이 될 수 있다.
 * - 객체가 null이 될 수 있다. -> 객체 그대롤를 리포지토리에 전달하면 예외가 발생하여 애플리케이션
 *   비정상적인 종료 될 수 있다.
 * - 따라서 validate() 메서드는 예기치 못한 오류 방지, 클라이언트에게 명확한 에러 메십지 반환
 *
 * 2. entity.getUserId()가 null인지 확인
 *  - Todo는 반드시 사용자에게 종속되어야 한다.
 *  - 만약 userId가 null인 상태로 저장된다면, 투두는 누구의 것인지 확인하기가 힘들다
 *  - 데이터베이스에 userId가 not null로 설정되면 저장시도 자체가 실패
 * */
