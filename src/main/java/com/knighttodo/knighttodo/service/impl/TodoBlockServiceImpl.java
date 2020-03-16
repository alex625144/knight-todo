package com.knighttodo.knighttodo.service.impl;

import com.knighttodo.knighttodo.domain.TodoBlockVO;
import com.knighttodo.knighttodo.exception.TodoNotFoundException;
import com.knighttodo.knighttodo.gateway.TodoBlockGateway;
import com.knighttodo.knighttodo.service.TodoBlockService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoBlockServiceImpl implements TodoBlockService {

    private final TodoBlockGateway todoBlockGateway;

    @Override
    public TodoBlockVO save(TodoBlockVO todoBlockVO) {
        return todoBlockGateway.save(todoBlockVO);
    }

    @Override
    public List<TodoBlockVO> findAll() {
        return todoBlockGateway.findAll();
    }

    @Override
    public TodoBlockVO findById(String blockId) {
        Optional<TodoBlockVO> result = todoBlockGateway.findById(blockId);

        if (result.isPresent()) {
            return result.get();
        }
        throw new RuntimeException("Did not find TodoBlock id - " + blockId);
    }

    @Override
    public TodoBlockVO updateTodoBlock(TodoBlockVO changedTodoBlockVO) {
        TodoBlockVO todoBlockVO = todoBlockGateway.findById(changedTodoBlockVO.getId())
            .orElseThrow(TodoNotFoundException::new);

        todoBlockVO.setId(changedTodoBlockVO.getId());
        todoBlockVO.setBlockName(changedTodoBlockVO.getBlockName());
        todoBlockVO.setTodos(changedTodoBlockVO.getTodos());
        return todoBlockGateway.save(todoBlockVO);
    }

    @Override
    public void deleteById(String blockId) {
        todoBlockGateway.deleteById(blockId);
    }
}
