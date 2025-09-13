package security.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> {
    // error 발생 시 에러 메시지를 반환
    private String error;

    // Generic을 이용해서 2xx 대 응답 시 해당 Type의 리스트를 반환
    private List<T> data;
}

/*
* ResponseDTO를 사용하는 이유
* - 응답 형식의 일관성을 유지하기 위해 사용
* - HTTP Response할 떄 사용하게 될 DTO
* - 서버에서 클라이언트로 응답할 때 사용할 데이터 구조 정의
* - ex) 로그인 성공시 -> 로그인 성공! 메세지가 옴
*   투두 조회시 -> [{id:1, title:study, done:true} , {id:2, title:sleep, done:true}]
*       일때 프론트에서 요청할 때 테스트를 많이하고 힘듬
*
*   제네릭 사용 이유
* - 리스트에 어떤 객체가 들어올지 모른다.
* - 프론트에서 전송할 데이터가 뭔지 몰라서 사용
* - 서버가 프론트엔드에게 여러 종류의 데이터를 동일한 응답 형식을 제공하기 위해
*
*   T를 사용한 이유
*   - 앞에서 T부분은 어떤 타입으로 정의하지 않아서이다.
*   -> 정확히 어떤 것이 들어올기 모르기 떄문이다.
*   -> 따라서 자유성을 보장하기 위해 사용
*   -> todo를 만들라고 힌트를 주기 위해 -> T부분에는 DTO을 사용하겠다고 약속
* */
