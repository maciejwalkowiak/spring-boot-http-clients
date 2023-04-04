package app;

import java.util.List;

import com.maciejwalkowiak.spring.http.annotation.EnableHttpClients;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(TodoClient todoClient) {
        return args -> {
            System.out.println(todoClient.get());
        };
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
