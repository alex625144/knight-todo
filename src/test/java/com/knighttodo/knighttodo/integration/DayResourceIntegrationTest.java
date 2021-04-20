package com.knighttodo.knighttodo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knighttodo.knighttodo.factories.DayFactory;
import com.knighttodo.knighttodo.factories.DayTodoFactory;
import com.knighttodo.knighttodo.gateway.privatedb.repository.DayRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.DayTodoRepository;
import com.knighttodo.knighttodo.gateway.privatedb.representation.Day;
import com.knighttodo.knighttodo.gateway.privatedb.representation.DayTodo;
import com.knighttodo.knighttodo.rest.request.DayRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.knighttodo.knighttodo.Constants.API_BASE_DAYS;
import static com.knighttodo.knighttodo.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = DayResourceIntegrationTest.DockerPostgreDataSourceInitializer.class)
@Testcontainers
public class DayResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private DayTodoRepository dayTodoRepository;

    @AfterEach
    public void tearDown() {
        dayRepository.deleteAll();
    }

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1");

    static {
        postgresqlContainer.start();
    }

    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresqlContainer.getUsername(),
                    "spring.datasource.password=" + postgresqlContainer.getPassword()
            );
        }
    }

    @Test
    void test() {
        assertTrue(postgresqlContainer.isRunning());
    }

    @Test
    public void addDay_shouldAddDayAndReturnIt_whenRequestIsCorrect() throws Exception {
        DayRequestDto requestDto = DayFactory.createDayRequestDto();

        mockMvc.perform(
            post(API_BASE_DAYS)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath(buildJsonPathToId()).exists());

        assertThat(dayRepository.count()).isEqualTo(1);
    }

    @Test
    public void addDay_shouldRespondWithBadRequestStatus_whenNameIsNull() throws Exception {
        DayRequestDto requestDto = DayFactory.createDayRequestDtoWithoutName();

        mockMvc.perform(
            post(API_BASE_DAYS)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());

        assertThat(dayRepository.count()).isEqualTo(0);
    }

    @Test
    public void addDay_shouldRespondWithBadRequestStatus_whenNameConsistsOfSpaces() throws Exception {
        DayRequestDto requestDto = DayFactory.createDayRequestDtoWithNameConsistingOfSpaces();

        mockMvc.perform(
            post(API_BASE_DAYS)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());

        assertThat(dayRepository.count()).isEqualTo(0);
    }

    @Test
    public void findAllDays_shouldReturnAllDays() throws Exception {
        dayRepository.save(DayFactory.dayInstance());
        dayRepository.save(DayFactory.dayInstance());

        mockMvc.perform(
            get(API_BASE_DAYS))
            .andExpect(status().isFound())
            .andExpect(jsonPath(buildJsonPathToLength()).value(2));
    }

    @Test
    public void findDayById_shouldReturnExistingDay_whenIdIsCorrect() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());

        mockMvc.perform(
            get(buildGetDayByIdUrl(day.getId())))
            .andExpect(status().isFound())
            .andExpect(jsonPath(buildJsonPathToId()).value(day.getId()));
    }

    @Test
    public void updateDay_shouldUpdateDayAndReturnIt_whenRequestIsCorrect() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());
        DayRequestDto requestDto = DayFactory.updateDayRequestDto();

        mockMvc.perform(put(API_BASE_DAYS + "/" + day.getId())
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath(buildJsonPathToDayName()).value(requestDto.getDayName()));
    }

    @Test
    public void updateDay_shouldRespondWithBadRequestStatus_whenDayNameIsNull() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());
        DayRequestDto requestDto = DayFactory.updateDayRequestDtoWithoutName();

        mockMvc.perform(put(API_BASE_DAYS + "/" + day.getId())
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateDay_shouldRespondWithBadRequestStatus_whenDayNameConsistsOfSpaces() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());
        DayRequestDto requestDto = DayFactory
            .updateDayRequestDtoWithNameConsistingOfSpaces();

        mockMvc.perform(put(API_BASE_DAYS + "/" + day.getId())
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateDay_shouldUpdateDayNameAndCheckReturnFields_whenResponseIsCorrect() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());

        DayTodo firstDayTodo = DayTodoFactory.dayTodoWithDayInstance(day);
        firstDayTodo.setDay(day);
        dayTodoRepository.save(firstDayTodo);
        DayTodo secondDayTodo = DayTodoFactory.dayTodoWithDayInstance(day);
        secondDayTodo.setDay(day);
        dayTodoRepository.save(secondDayTodo);

        DayRequestDto dayRequestDto = DayFactory.createDayRequestDto();

        mockMvc.perform(put(API_BASE_DAYS + "/" + day.getId())
            .content(objectMapper.writeValueAsString(dayRequestDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath(buildJsonPathToDayName()).value(dayRequestDto.getDayName()))
            .andExpect(jsonPath(buildJsonPathToId()).exists());

        assertThat(dayRepository.count()).isEqualTo(1);
        assertThat(dayTodoRepository.count()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void deleteDay_shouldDeleteDay_whenIdIsCorrect() throws Exception {
        Day day = dayRepository.save(DayFactory.dayInstance());

        mockMvc.perform(delete(buildDeleteDayByIdUrl(day.getId())))
            .andExpect(status().isOk());

        assertThat(dayRepository.findById(day.getId())).isEmpty();
    }
}