package app;

import java.util.List;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.web.service.annotation.GetExchange;

@HttpClient("user-client")
public interface UserClient {
    @GetExchange
    List<User> get();
}
