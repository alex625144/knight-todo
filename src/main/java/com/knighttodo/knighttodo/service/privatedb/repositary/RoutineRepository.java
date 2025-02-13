package com.knighttodo.knighttodo.service.privatedb.repositary;

import com.knighttodo.knighttodo.service.privatedb.representation.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {

    @Query("select r from Routine r where r.id=:routineId and r.removed=false")
    Optional<Routine> findByIdAlive(@Param("routineId")UUID routineId);

    @Query("select r from Routine r where r.removed=false")
    List<Routine> findAllAlive();

    @Modifying
    @Query("update RoutineInstance ri set ri.removed=true where ri.routine.id=:routineId")
    void softDeleteAllRoutineInstancesByRoutineId(@Param("routineId") UUID routineId);

    @Modifying
    @Query("update RoutineTodo rt set rt.removed=true  where rt.routine.id=:routineId")
    void softDeleteAllRoutineTodosByRoutineId(@Param("routineId") UUID routineId);

    @Modifying
    @Query("update Routine r set r.removed=true where r.id=:routineId")
    void softDeleteById(@Param("routineId") UUID routineId);
}
