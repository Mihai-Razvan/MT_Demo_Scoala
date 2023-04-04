package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.services.TeachersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RestTeachersControllerTest {

    MockMvc mockMvc;
    @Autowired
    RestTeachersController restTeachersController;
    @MockBean
    TeachersService teachersServiceMock;

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

        when(teachersServiceMock.getTeachers()).thenReturn(List.of(teacher1, teacher2));

        mockMvc.perform(get("/rest/teachers/show"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Prenume1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Nume1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes[0].code").value("ASD432"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Prenume2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value("Nume2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes[0].code").value("ASD432"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes[1].code").value("HSRE2R"));
    }
}