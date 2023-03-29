package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.services.StudentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RestStudentsControllerTest {

    MockMvc mockMvc;
    @Autowired
    RestStudentsController restStudentsController;
    @MockBean
    StudentsService studentsServiceMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restStudentsController).build();
    }

    @Test
    void shouldReturnOneStudentInTheGivenClass() throws Exception{
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student = new Student("Andrei", "Ionut",13, clasa);

        when(studentsServiceMock.getStudentsByClass(classCode)).thenReturn(List.of(student).toString());

        MvcResult result = mockMvc.perform(get("/rest/students/show?classCode=" + classCode)).andReturn();
        String actual = result.getResponse().getContentAsString();
        assertEquals(List.of(student).toString(), actual);

        mockMvc.perform(get("/rest/students/show?classCode=" + classCode))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Ionut"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(13))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.profile").value("Mate-Info"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.number").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.code").value(classCode));
    }

    @Test
    void shouldAddOneStudentToClasAndReturnThatClass() throws Exception {
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, "C24GV");
        Student oldStudent = new Student("Ion", "Marcel", 14, clasa);
        Student newStudent = new Student("Andreea", "Ioana", 15, clasa);

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(studentsServiceMock.addStudent(body)).thenReturn(List.of(oldStudent, newStudent).toString());

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Ion"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Andreea"));
    }

    @Test
    void shouldNotAddStudentAndReturnError() throws Exception{
        String classCode = "C24GV";

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(studentsServiceMock.addStudent(body)).thenReturn("ERROR");

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }

    @Test
    void shouldMoveStudentAndReturnTheStudentsInClassWhereMoved() throws Exception{
        String newClassCode = "C24GV";
        Class newClass = new Class("Filologie", 7, newClassCode);
        Class oldClass = new Class("Mate-Info", 7, "SDGF3");
        Student existingStudent = new Student("Andreea", "Ioana", 13, newClass);
        Student movedStudent = new Student("Ion", "Marcel", 14, oldClass);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\",\n" +
                "     \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");
        body.put("newClassCode", newClassCode);

        when(studentsServiceMock.moveStudent(body)).thenReturn(List.of(existingStudent, movedStudent).toString());

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andreea"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Ion"));
    }

    @Test
    void shouldNotMoveStudentAndReturnError() throws Exception{
        String newClassCode = "C24GV";

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\",\n" +
                "     \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");
        body.put("newClassCode", newClassCode);

        when(studentsServiceMock.moveStudent(body)).thenReturn("ERROR");

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }

    @Test
    void shouldDeleteStudentAndReturnOneRemainingStudentInThatClass() throws Exception{
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student remainingStudent = new Student("Andreea", "Ioana", 13, clasa);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");

        when(studentsServiceMock.deleteStudent(body)).thenReturn(List.of(remainingStudent).toString());
        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Andreea"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Ioana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(13))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.profile").value("Filologie"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.number").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].clasa.code").value(classCode));
    }

    @Test
    void shouldNotDeleteStudentAndReturnError() throws Exception{;
        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\"\n" +
                "}";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");

        when(studentsServiceMock.deleteStudent(body)).thenReturn("ERROR");

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ERROR"));
    }
}