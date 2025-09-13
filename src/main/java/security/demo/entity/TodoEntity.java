package security.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Todo") // 테이블 연결
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 여긴 왜 참조형으로 작성하는지 질문 -> 내생각: 아마 테이블은 참조형이여서?

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "done", nullable = false)
    private boolean done;
}

/*
* 어노테이션으로 테이블연결 -> 테이블 이름 매칭
* */
