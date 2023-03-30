package com.example.demo_scoala.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:~/testDb"})
@Transactional
class RestStudentsControllerIT {

    MockMvc mockMvc;
    @Autowired
    RestStudentsController restStudentsController;
    ObjectMapper mapper;
    Map<String, String> body;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restStudentsController).build();
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        body = new HashMap<>();
    }

    @Test
    @Sql("students_controller_data_1.sql")
    void shouldReturnOneStudentInTheGivenClass() throws Exception {
        String classCode = "ABC123";

        mockMvc.perform(get("/rest/students/show?classCode=" + classCode))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Ion"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(13))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.code").value("ABC123"));
    }

    @Test
    @Sql("students_controller_data_2.sql")
    void shouldAddOneStudentToClasAndReturnThatStudent() throws Exception {
        String classCode = "ABC123";

        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Andreea"))
                .andExpect(MockMvcResultMatchers.jsonPath("clasa.code").value(classCode));
    }

    @Test
    void shouldNotFindClassAndReturnNull() throws Exception {   //no class found -> null
        String classCode = "ABC123";

        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldMoveStudentAndReturnThatStudent() throws Exception {
        String newClassCode = "DEF456";

        body.put("firstName", "Andrei");
        body.put("lastName", "Ion");
        body.put("newClassCode", newClassCode);

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Andrei"))
                .andExpect(MockMvcResultMatchers.jsonPath("clasa.code").value(newClassCode));
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldNotFindGivenStudentAndReturnNull() throws Exception {      //no student found -> null
        String newClassCode = "DEF456";

        body.put("firstName", "Gicu");
        body.put("lastName", "Ion");
        body.put("newClassCode", newClassCode);

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldNotFindGivenClassAndReturnNull() throws Exception {      //no class found -> null
        String newClassCode = "FGQ652";

        body.put("firstName", "Gicu");
        body.put("lastName", "Ion");
        body.put("newClassCode", newClassCode);

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    @Sql("students_controller_data_4.sql")
    void shouldDeleteStudentAndReturnThatStudent() throws Exception {
        body.put("firstName", "Andrei");
        body.put("lastName", "Ion");

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Andrei"));
    }

    @Test
    @Sql("students_controller_data_4.sql")
    void shouldNotFindStudentAndReturnNull() throws Exception {      //no student found -> null
        body.put("firstName", "Gicu");
        body.put("lastName", "Ion");

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }
}