package com.example.demo_scoala.restcontrollers;

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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:/home/mihai/Intellij-java11/Demo_Scoala/testDb"})
@Transactional
class RestStudentsControllerIT {

    MockMvc mockMvc;
    @Autowired
    RestStudentsController restStudentsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restStudentsController).build();
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
    void shouldAddOneStudentToClasAndReturnThatClass() throws Exception {
        String classCode = "ABC123";

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Andreea"));
    }

    @Test
    void shouldNotFindClassAndReturnError() throws Exception {   //no class found -> error
        String classCode = "ABC123";

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldMoveStudentAndReturnThatClass() throws Exception {
        String newClassCode = "DEF456";

        String requestBody = "{\n" +
                "    \"firstName\": \"Andrei\",\n" +
                "    \"lastName\": \"Ion\",\n" +
                "    \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.code").value("DEF456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Marcel"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].clasa.code").value("DEF456"));
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldNotFindGivenStudentAndReturnError() throws Exception {      //no student found -> error
        String newClassCode = "DEF456";

        String requestBody = "{\n" +
                "    \"firstName\": \"Gicu\",\n" +
                "    \"lastName\": \"Ion\",\n" +
                "    \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }

    @Test
    @Sql("students_controller_data_3.sql")
    void shouldNotFindGivenClassAndReturnError() throws Exception {      //no class found -> error
        String newClassCode = "FGQ652";

        String requestBody = "{\n" +
                "    \"firstName\": \"Andrei\",\n" +
                "    \"lastName\": \"Ion\",\n" +
                "    \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }

    @Test
    @Sql("students_controller_data_4.sql")
    void shouldDeleteStudentAndReturnThatClass() throws Exception {
        String requestBody = "{\n" +
                "    \"firstName\": \"Andrei\",\n" +
                "    \"lastName\": \"Ion\"\n" +
                "}";

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Marcel"));
    }

    @Test
    @Sql("students_controller_data_4.sql")
    void shouldNotFindStudentAndReturnError() throws Exception {      //no student found -> error
        String requestBody = "{\n" +
                "    \"firstName\": \"Gicu\",\n" +
                "    \"lastName\": \"Ion\"\n" +
                "}";

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }
}