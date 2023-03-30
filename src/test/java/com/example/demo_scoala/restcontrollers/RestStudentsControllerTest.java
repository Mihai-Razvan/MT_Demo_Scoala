package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.services.StudentsService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
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
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restStudentsController).build();
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    void shouldReturnOneStudentInTheGivenClass() throws Exception{
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student = new Student("Andrei", "Ionut",13, clasa);

        when(studentsServiceMock.getStudentsByClass(classCode)).thenReturn(List.of(student));

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
    void shouldAddOneStudentToClasAndReturnThatStudent() throws Exception {
        String classCode = "HE632H";
        Class clasa = new Class("Filologie", 7, classCode);
        Student student = new Student("Andreea", "Ioana", 14, clasa);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(studentsServiceMock.addStudent(body)).thenReturn(student);

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Andreea"))
                .andExpect(MockMvcResultMatchers.jsonPath("clasa.code").value(classCode));
    }

    @Test
    void shouldNotAddStudentAndReturnNull() throws Exception{
        String classCode = "C24GV";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(studentsServiceMock.addStudent(body)).thenReturn(null);

        mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void shouldMoveStudentAndReturnThatStudent() throws Exception{
        String newClassCode = "C24GV";
        String oldClassCode = "SDGF3V";
        Class newClass = new Class("Mate-Info", 7, newClassCode);
        Student movedStudent = new Student("Ion", "Marcel", 14, newClass);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");
        body.put("newClassCode", newClassCode);

        when(studentsServiceMock.moveStudent(body)).thenReturn(movedStudent);

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Ion"))
                .andExpect(MockMvcResultMatchers.jsonPath("clasa.code").value(newClassCode));
    }

    @Test
    void shouldNotMoveStudentAndReturnNull() throws Exception{
        String newClassCode = "C24GV";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");
        body.put("newClassCode", newClassCode);

        when(studentsServiceMock.moveStudent(body)).thenReturn(null);

        mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void shouldDeleteStudentAndReturnOneRemainingStudentInThatClass() throws Exception{
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student student = new Student("Andreea", "Ioana", 13, clasa);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");

        when(studentsServiceMock.deleteStudent(body)).thenReturn(student);
        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("Andreea"))
                .andExpect(MockMvcResultMatchers.jsonPath("lastName").value("Ioana"))
                .andExpect(MockMvcResultMatchers.jsonPath("clasa.code").value(classCode));
    }

   @Test
    void shouldNotDeleteStudentAndReturnNUll() throws Exception{;
        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Ion");
        body.put("lastName", "Marcel");

        when(studentsServiceMock.deleteStudent(body)).thenReturn(null);

        mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }
}