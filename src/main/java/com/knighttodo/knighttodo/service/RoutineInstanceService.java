package com.knighttodo.knighttodo.service;

import com.knighttodo.knighttodo.domain.RoutineInstanceVO;
import com.knighttodo.knighttodo.domain.RoutineTodoInstanceVO;
import com.knighttodo.knighttodo.domain.RoutineVO;
import com.knighttodo.knighttodo.exception.RoutineInstanceNotFoundException;

import com.knighttodo.knighttodo.service.privatedb.mapper.RoutineInstanceMapper;
import com.knighttodo.knighttodo.service.privatedb.mapper.RoutineTodoInstanceMapper;
import com.knighttodo.knighttodo.service.privatedb.repositary.RoutineInstanceRepository;
import com.knighttodo.knighttodo.service.privatedb.repositary.RoutineTodoInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class RoutineInstanceService {


    private final RoutineService routineService;
    private final RoutineInstanceRepository routineInstanceRepository;
    private final RoutineInstanceMapper routineInstanceMapper;
    private final RoutineTodoInstanceRepository routineTodoInstanceRepository;
    private final RoutineTodoInstanceMapper routineTodoInstanceMapper;

    @Transactional
    public RoutineInstanceVO save(RoutineInstanceVO routineInstanceVO, UUID routineId) {
        RoutineVO foundRoutine = routineService.findById(routineId);
        routineInstanceVO.setRoutine(foundRoutine);
        return routineInstanceMapper.toRoutineInstanceVO(routineInstanceRepository.save(
                routineInstanceMapper.toRoutineInstance(routineInstanceVO)
        ));
    }

    public List<RoutineInstanceVO> findAll() {
        return routineInstanceRepository.findAllAlive().stream().map(routineInstanceMapper::toRoutineInstanceVO).collect(Collectors.toList());
    }

    @Transactional
    public RoutineInstanceVO findById(UUID routineInstanceId) {
        RoutineInstanceVO routineInstanceVO = findRoutineInstanceVO(routineInstanceId);
        List<RoutineTodoInstanceVO> routineTodoInstances = routineTodoInstanceRepository.findByRoutineInstanceIdAlive(routineInstanceId).stream()
                .map(routineTodoInstanceMapper::toRoutineTodoInstanceVO)
                .collect(Collectors.toList());
        routineService.updateRoutineTodoInstances(routineInstanceVO.getRoutine().getId(), routineTodoInstances);
        return routineInstanceVO;
    }

    public RoutineInstanceVO findRoutineInstanceVO(UUID routineInstanceId) {
        return routineInstanceRepository.findByIdAlive(routineInstanceId).map(routineInstanceMapper::toRoutineInstanceVO)
                .orElseThrow(() -> {
                    log.error(String.format("Routine Instance with such id:%s can't be " + "found", routineInstanceId));
                    return new RoutineInstanceNotFoundException(
                            String.format("Routine Instance with such id:%s can't be " + "found", routineInstanceId));
                });
    }

    @Transactional
    public RoutineInstanceVO update(UUID routineInstanceId, RoutineInstanceVO changedRoutineInstanceVO) {
        RoutineInstanceVO routineInstanceVO = findById(routineInstanceId);
        routineInstanceVO.setName(changedRoutineInstanceVO.getName());
        routineInstanceVO.setHardness(changedRoutineInstanceVO.getHardness());
        routineInstanceVO.setScariness(changedRoutineInstanceVO.getScariness());
        routineInstanceVO.setReady(changedRoutineInstanceVO.isReady());
        return routineInstanceMapper.toRoutineInstanceVO(routineInstanceRepository.save(
                routineInstanceMapper.toRoutineInstance(routineInstanceVO)));
    }

    @Transactional
    public void deleteById(UUID routineInstanceId) {
        routineInstanceRepository.softDeleteAllRoutineTodoInstancesByRoutineInstanceId(routineInstanceId);
        routineInstanceRepository.softDeleteById(routineInstanceId);
    }
}
