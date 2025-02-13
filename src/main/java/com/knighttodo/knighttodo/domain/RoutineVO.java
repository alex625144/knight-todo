package com.knighttodo.knighttodo.domain;

import com.knighttodo.knighttodo.service.privatedb.representation.enums.Hardness;
import com.knighttodo.knighttodo.service.privatedb.representation.enums.Scariness;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"routineInstanceVOs", "routineTodos"})
public class RoutineVO {

    private UUID id;

    private String name;

    private Hardness hardness;

    private Scariness scariness;

    private List<RoutineInstanceVO> routineInstanceVOs;

    private List<RoutineTodoVO> routineTodos;
}
