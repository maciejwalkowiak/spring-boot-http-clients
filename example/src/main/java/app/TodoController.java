package app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TodoController {
    private final TodoClient todoClient;

    TodoController(TodoClient todoClient) {
        this.todoClient = todoClient;
    }

    @GetMapping("/todos")
    List<Todo> todos() {
        return todoClient.get();
    }
}
