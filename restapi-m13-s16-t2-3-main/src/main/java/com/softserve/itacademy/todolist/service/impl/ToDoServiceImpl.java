package com.softserve.itacademy.todolist.service.impl;

import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.repository.ToDoRepository;
import com.softserve.itacademy.todolist.service.ToDoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository todoRepository;

    public ToDoServiceImpl(ToDoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ToDo create(ToDo todo) {
        if (todo != null) {
            return todoRepository.save(todo);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ToDo not found");
    }

    @Override
    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo with id " + id + " not found"));
    }

    @Override
    public ToDo update(ToDo todo) {
        if (todo != null) {
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ToDo not found");
    }

    @Override
    public void delete(long id) {
        ToDo todo = readById(id);
        todoRepository.delete(todo);
    }

    @Override
    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    @Override
    public List<ToDo> getByUserId(long userId) {
        if(todoRepository.getByUserId(userId) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found");
        }
        else{
            return todoRepository.getByUserId(userId);
        }
    }
}
