# Spring Boot HTTP Clients

**Spring Boot HTTP Clients** provides zero-config auto-configuration for `WebClient` and Spring 6 HTTP Interface based HTTP clients in a **Spring Boot** application.

> **Note**
> Project is in early stage, expect breaking changes.


## 🤔 How to install

Project is not yet published to Maven central. 
To use it, you must checkout the repository and build locally with `./mvn install`.

Then, add the dependency to `spring-boot-http-clients`:

```xml
<dependency>
    <groupId>com.maciejwalkowiak.spring</groupId>
    <artifactId>spring-boot-http-clients</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## ✨ How to use

1. Define HTTP clients in `application.yml` or `application.properties` under prefix `http.clients`:

```yaml
http.clients:
  todo-client:
    url: https://jsonplaceholder.typicode.com/todos
  user-client:
    url: https://jsonplaceholder.typicode.com/users
```

The client names (in above example `todo-client` and `user-client`) are just strings - use anything that makes sense - they are going to be used to construct the `WebClient` bean name.

Above code gets processed by `WebClientsAutoConfiguration` that creates a bean of type `WebClient` with name `<client-name>.WebClient`.

2. Define HTTP client with Spring 6 HTTP Interface, annotate it with `@HttpClient` and set the client name as the parameter:

```java 
@HttpClient("todo-client")
public interface TodoClient {
    @GetExchange
    List<Todo> get();
}
```

3. That's it! Now you can inject `TodoClient` anywhere in your code 🙃

Sounds good? Consider [❤️ Sponsoring](https://github.com/sponsors/maciejwalkowiak) the project! Thank you!
