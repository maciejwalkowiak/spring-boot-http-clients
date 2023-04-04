package app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {
    private final UserClient userClient;

    UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/users")
    List<User> users() {
        return userClient.get();
    }
}
