package security.demo.controller;

import com.fasterxml.jackson.databind.deser.CreatorProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import security.demo.dto.ResponseDTO;
import security.demo.dto.TodoDTO;
import security.demo.entity.TodoEntity;
import security.demo.service.TodoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoService service;

    /*
     * ResponseEntity 란?
     * - 해당 객체를 이용해 상태코드, 응답 본문 등을 설정해서 클라이언트 응답
     * - HTTP 응답의 상태코드와 헤더를 포함해 더 세부적으로 제어
     *
     * ResponseEntity 메서두
     * - ok(): 성공
     * - body():응답 본문  설정
     *
     * @AuthenticationPrincipal
     *  - 현재 인증된 사용자 정봅에 접근할 수 있게한다.
     *  - spring security는 sercurity context에서 현재 인증된 사용자의 principal을 가져옴
     *  - principal: 주체 또는 주인을 의미 스프링 시큐리티에에서 현재 애플리케이션 접근하는 주체를 나타내는 객체
     *  - 우리코드에서   jwtAuthenticationFilter 클래스에서 userId 바탕으로 이중 객쳏함
     * */
    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            // TODO: 임시 유저로 하드코딩한 부분으로 추후 로그인된 유저로 변경 필요
            // 아직 유저를 구현하지 않아 임시아이디 생성
            String temporaryUserId = "temporary-user";

            // 1. DTO를 Entity로 변환하는 과정
            TodoEntity entity = TodoDTO.toEntity(dto);

            // 2. 생성하는 당시에는 id(PK)는 null 초기화 -> 데이터베이스 들어가기 전이라 가능
            // 새로 생성하는 레코드(행)이기 떄문이다.
            entity.setId(null);

            // 3. 유저 아이디 설정("누가" 생성한 투두인지를 설정)
            // TODO: 임시 유저로 하드코딩한 부분으로 추후 로그인된 유저로 변경 필요
            // entity.setUserId(temporaryUserId);
            // 기존 temporaryUserId 대신 매개변수로 넘어온 userId로 설정
            entity.setUserId(userId);

            // 4. 서비스 계층을 이용해 todo 엔티티 생성
            List<TodoEntity> entities = service.create(entity);

            // 5 리턴된 엔티티 리스트르르 TodoDTO로 변환
            // 아래에는 생성자로 초기화해서 작성가능
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            //List<TodoDTO> dtos = new ArrayList<>();
            //for(TodoEntity entity1 : entities) {
            //    TodoDTO dto1 = new TodoDTO(entity1);
            //    dtos.add(dto1);
            //}

            // 6 변환된 todoDTO 리스트를 이용해 ResponseDTO 초기화
            // 응답 객체 생성 -> TodoDTO를 만들고자 제네릭 처리 -> ResponseDTO에 있는 빌더 패턴 사용
            // -> data() 사용해 디티오에서 만든 필드를 다 사용 -> 객체 사용
            // 따라서 TodoDTO 타입을 담는 ResponseDTO 객체를 빌드
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .data(dtos).build();

            // 7 ResponseDTO를 클라이언트에게 응답
            // ResponseEntity.ok(): http 상태코드로 200으로 설정
            // body(): 응답의 body를 response 인스턴스(객체)로 설정
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 8 예외가 발생한 경우, ResponseDTO data 필드 대신, error 필드에 여러 메시지를 넣어서 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 여기서 @RequestBody TodoDTO는 왜 안받아 오는가? -> 일반적인 Get 요청은 서버에서 사용
    // 지금은 본문에 데이터가 포함되어있지 않고, 쿼리 파라미터로 특정아이디를 조회하지 않기 때문이다.
    @GetMapping
    public ResponseEntity<?> read(@AuthenticationPrincipal String userId) {
        // TODO: 임시 유저 하드코딩으로 추후 수정 필요
        //String temporaryUserId = "temporary-user";

        // 1. 서비스 계층의 retreive 메서드를 사용해 투두 리스트(엔티티) 가져오기
        // -> 왜 디티오 먼저 안가져오지 메소드 반환이 엔티티여서 바꾸는건가?
        // -> 계층(Layer)간의 역할 분리라는 설계의 원칙 때문이다.
        // 서비스:데이터베이스와 가장 가까운 곳에서 비즈니스(TodoEntity) 로직을 처리한다.
        // 컨트롤러: 클라이언트와 소통하는 역할
        // 서비스에서 데이터베이스 조회 -> todoEntity 반환 -> 클라어트로 보낼때 DTO 변환
        List<TodoEntity> entities = service.retrieve(userId);

        // 2.리턴된 엔티티 리스트를 TodoDTO 리스트로 변환 -> 이건 서비스-> 컨트롤러 디티오 형식으로 받아야해서 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // 3. 변환된 TodoDTO 리스트를 이용해서 응답형태인 ResponseDTO를 초기화 -> 2. 리턴된걸 출력하면 안돼나?
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // 4. ResponsDTO를 리턴 -> 아래에는 ResponseEntity 이유는 반환타입이기 떄문에
        return ResponseEntity.ok().body(response);
    }

    /*
     * 수정은 TodoEntity의 아이디의 경로로 수정한다.
     * 해당 유저는 인증이 된 유저로서 로그인시 자기가 작성한 투두만 수정 가능
     *
     * 1. 서비스 계층을 호출하여 todo 업데이트하고, 단일 DTO 반환
     * 2. 웅답 객체를 빌드하여 HTTP 200 상태코드로 변환
     * 3. 예외가 발생시 HTTP4 400 상태코드로 변환
     */
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @AuthenticationPrincipal String userId,
                                    @RequestBody TodoDTO dto) {
        try {
            TodoDTO update = service.update(id, userId, dto);

            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .data(Collections.singletonList(update))
                    .build();
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .error(error)
                    .build();

            return ResponseEntity.badRequest().body("존재하지 않습니다.");
        }
    }

   /*
   * 1. 서비스 계층의 삭제 메소드 호출
   * 2. 삭제 성공시 200 리턴
   * 3. 예외 발생시 400 리턴
   * */
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id,
                                    @AuthenticationPrincipal String userId) {

     try {
         service.delete(id, userId);

         return ResponseEntity.ok().body("삭제 성공");
     }catch (Exception e) {
         String error = e.getMessage();
         ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                 .error(error)
                 .build();

         return ResponseEntity.badRequest().body("존재하지 않습니다.");
     }
    }
}
