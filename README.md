# Spring Boot HTTP Clients

**Spring Boot HTTP Clients** provides zero-boilerplate auto-configuration for `WebClient` and Spring 6 HTTP Interface based HTTP clients in a **Spring Boot** application.

> **Note**
> Project is in early stage, expect breaking changes.

Spring 6 introduced a new way to define HTTP clients using interfaces - [HTTP Interfaces](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#rest-http-interface) (introduction video: [🚀 New in Spring Framework 6: HTTP Interfaces](https://www.youtube.com/watch?v=A1V71peRNn0).

Right now, setting up an HTTP client requires a bit of boilerplate code:

1. Create a property for the base url. 
2. Create a `WebClient` bean which uses this property
3. Create a `HttpServiceProxyFactory`.

This project is meant to simplify this process and provide ease of creating HTTP clients similar to [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/).

> **Note**
> Very likely, Spring Boot will eventually implement its own way to autoconfigure HTTP clients and this project will become deprecated. Look for updates on this topic here: [#31337](https://github.com/spring-projects/spring-boot/issues/31337)

## 🤔 How to install

Project is not yet published to Maven central. 
To use it, you must checkout the repository and build locally with `./mvn install`.

Then, add the dependency to `spring-boot-http-clients`:

```xml
<dependency>
    <groupId>com.maciejwalkowiak.spring</groupId>
    <artifactId>spring-boot-http-clients</artifactId>
    <version>0.1.0</version>
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

## Advanced usage

Autoconfigured `WebClient` instances are the result of calling Spring Boot autoconfigured `WebClient.Builder#build` 
which allow to define custom `WebClientCustomizer` that get applied on the `WebClient` beans.

If for any reason you cannot rely on the autoconfigured `WebClient`, but you still want to use autoconfigured HTTP Interface 
based HTTP client, make sure to create a bean with name `<client-name>.WebClient` which will be picked up to create an HTTP client. 

## Contributing

If you found a bug or a missing feature - you're very welcome to submit an issue and a pull request with a fix.
Note, that this library is intended only to glue things together and not to compensate on the missing features in Spring's support for HTTP interfaces.

Sounds good? Consider [❤️ Sponsoring](https://github.com/sponsors/maciejwalkowiak) the project! Thank you!
