package com.knighttodo.todocore.character.gateway;

import com.knighttodo.todocore.character.domain.SkillVO;
import com.knighttodo.todocore.character.gateway.privatedb.mapper.SkillMapper;
import com.knighttodo.todocore.character.gateway.privatedb.repository.SkillRepository;
import com.knighttodo.todocore.character.gateway.privatedb.representation.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillGatewayImpl implements SkillGateway {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillVO save(SkillVO skillVO) {
        Skill skill = skillMapper.toSkill(skillVO);
        return skillMapper.toSkillVO(skillRepository.save(skill));
    }

    @Override
    public List<SkillVO> findAll() {
        return skillRepository.findAll().stream().map(skillMapper::toSkillVO).collect(Collectors.toList());
    }

    @Override
    public Optional<SkillVO> findById(String skillId) {
        return skillRepository.findById(skillId).map(skillMapper::toSkillVO);
    }

    @Override
    public void deleteById(String skillId) {
        skillRepository.deleteById(skillId);
    }
}