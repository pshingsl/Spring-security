package security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import security.demo.dto.ResponseDTO;
import security.demo.dto.TodoDTO;
import security.demo.entity.TodoEntity;
import security.demo.service.TodoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/api/todo")
@RequestMapping
public class TodoController {

    @Autowired
    private TodoService service;

    /*
    * */
    public ResponseEntity<?> create(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);
            entity.setId(null);
            entity.setUserId(userId);
            List<TodoEntity> entities = service.create(entity);
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .data(dtos).build();
            return ResponseEntity.ok().body(dtos);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder()
                    .error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }
}
