package app;

import java.util.List;

import app.Todo;
import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.web.service.annotation.GetExchange;

@HttpClient("todo-client")
public interface TodoClient {
    @GetExchange("/todos")
    List<Todo> get();
}
