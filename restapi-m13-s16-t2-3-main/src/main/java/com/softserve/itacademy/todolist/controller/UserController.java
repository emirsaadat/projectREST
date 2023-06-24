package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.TaskResponse;
import com.softserve.itacademy.todolist.dto.ToDoResponse;
import com.softserve.itacademy.todolist.dto.UserResponse;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable("id") long id) {
        User user = userService.readById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return new UserResponse(user);
    }
    @PutMapping("/{id}/update")
    public UserResponse updateUser(@PathVariable("id") long id, @RequestBody User user) {
        user.setId(id);
        user.setEmail(userService.readById(id).getEmail());
        user.setRole(userService.readById(id).getRole());
        user.setPassword(userService.readById(id).getPassword());
        user.setFirstName(userService.readById(id).getFirstName());
        user.setLastName(userService.readById(id).getLastName());
        user.setMyTodos(userService.readById(id).getMyTodos());
        user.setOtherTodos(userService.readById(id).getOtherTodos());
        return new UserResponse(userService.update(user));
    }

    @PostMapping("/create")
    public List<UserResponse> createUser(@RequestBody User user) {
        userService.create(user);
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

    }
    @DeleteMapping("/{id}/delete")
    public List<UserResponse> deleteUser(@PathVariable("id") long id) {
        userService.delete(id);
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

}
