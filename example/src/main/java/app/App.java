package app;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

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
