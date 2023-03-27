package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.JsonTransform;
import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
class RestTeachersControllerTest {

    MockMvc mockMvc;
    @Autowired
    RestTeachersController restTeachersController;
    @MockBean
    StudentsRepository studentsRepositoryMock;
    @MockBean
    ClassesRepository classesRepositoryMock;
    @MockBean
    JsonTransform jsonTransformMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restTeachersController).build();
    }

    @Test
    void shouldReturnTwoTeachers() throws Exception{
        Class clasa1 = new Class("Mate-Info", 7, "ASD432");
        Class clasa2 = new Class("Filologie", 11, "HSRE2R");
        Teacher teacher1 = new Teacher("Prenume1", "Nume1", "Biologie", List.of(clasa1));
        Teacher teacher2 = new Teacher("Prenume2", "Nume2", "Chimie", List.of(clasa1, clasa2));
        Map<String, List<Teacher>> map = new HashMap<>();
        map.put("teachers", List.of(teacher1, teacher2));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String expected = mapper.writeValueAsString(map);

        when(jsonTransformMock.teachersToJson()).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/rest/teachers/show")).andReturn();
        String actual = result.getResponse().getContentAsString();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnError() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.get("/rest/teachers/show");

        when(jsonTransformMock.teachersToJson()).thenThrow(JsonProcessingException.class);
        MvcResult result = mockMvc.perform(request).andReturn();
        String actual = result.getResponse().getContentAsString();

        assertEquals("ERROR", actual);
    }
}