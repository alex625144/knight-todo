package com.knighttodo.todocore.character.rest.mappers;

import com.knighttodo.todocore.character.domain.ExperienceVO;
import com.knighttodo.todocore.character.rest.request.ExperienceRequestDto;
import com.knighttodo.todocore.character.rest.response.ExperienceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceRestMapper {

    ExperienceVO toExperienceVO(ExperienceRequestDto experienceRequestDto);

    ExperienceResponseDto toExperienceResponseDto(ExperienceVO experienceVO);
}