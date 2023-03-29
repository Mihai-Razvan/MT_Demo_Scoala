package com.example.demo_scoala.restcontrollers;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:/home/mihai/Intellij-java11/Demo_Scoala/testDb"})
@Transactional
class RestTeachersControllerIT {

    MockMvc mockMvc;
    @Autowired
    RestTeachersController restTeachersController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restTeachersController).build();
    }

    @Test
    @Sql("teachers_controller_data_1.sql")
    void shouldReturnTwoTeachers() throws Exception {
        mockMvc.perform(get("/rest/teachers/show"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Prenume1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes[0].code").value("ABC123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].classes[1].code").value("DEF456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Prenume2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].classes[0].code").value("ABC123"));
    }
}