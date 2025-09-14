package security.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import security.demo.entity.TodoEntity;

import javax.swing.text.html.parser.Entity;

@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoDTO {
    private Long id;
    private String title;
    private boolean done;

    // TodoEntity 매개변수로 받아서 DTO 객체로 변환하는 생성자
    // -> 왜 만들어야 하는가?
    // 1. 서비스 계층과 컨트롤러 계층 간의 데이터 전달을 간결하기 위해서
    // 2. 서비스 계층에서 데이터베이스로 TodoEntity를 가져온 후, 이 엔티티를 클라이언트에게 직접 반환x
    // 3. DTO는 필요한 데이터만 담는다. -> 클라이언트 요청에 따라 값을 반환하기 귀해서이다.
    public TodoDTO(final TodoEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.done = entity.isDone();
    }

    // DTO를 Entity로 변환하는 메소드
    // -> 왜 만들어야 하는가?
    // 1. 클라이언트가 보낸 데이터를 데이터베이스에 저장하기 위해 DTO를 Entity로 변환한다.
    // 2. 클라이언트는 주로 JSON형태의 DTO데이터를 보내므로, JPA가 인식할 수 있는 Entity객체로 변환 해야한다.
    public static TodoEntity toEntity(final TodoDTO dto) {
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .done(dto.isDone())
                .build();
    }
    // 리턴 부분에서 userId를 받지 않는 이유
    // 클라이언트로부터 직접 받아서 안되는 중요한 정보이기 떄문이다.
    // JWT토큰에서 추출하혀 서버에서 직접 설정해야 하는 값이기 때문이다.
}
