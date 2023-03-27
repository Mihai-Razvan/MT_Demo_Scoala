package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.JsonTransform;
import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
class RestStudentsControllerTest {

    MockMvc mockMvc;
    @Autowired
    RestStudentsController restStudentsController;
    @MockBean
    StudentsRepository studentsRepositoryMock;
    @MockBean
    ClassesRepository classesRepositoryMock;
    @MockBean
    JsonTransform jsonTransformMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restStudentsController).build();
    }

    @Test
    void shouldReturnOneStudentInTheGivenClass() throws Exception{
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student = new Student("Andrei", "Ionut",13, clasa);
        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", List.of(student));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String expected = mapper.writeValueAsString(map);

        when(jsonTransformMock.studentsToJson(classCode)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/rest/students/show?classCode=" + classCode)).andReturn();
        String actual = result.getResponse().getContentAsString();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnError() throws Exception{
        String classCode = "HE6234F";
        RequestBuilder request = MockMvcRequestBuilders.get("/rest/students/show?classCode=" + classCode);

        when(jsonTransformMock.studentsToJson(classCode)).thenThrow(JsonProcessingException.class);
        MvcResult result = mockMvc.perform(request).andReturn();
        String actual = result.getResponse().getContentAsString();

        assertEquals("ERROR", actual);
    }

    @Test
    void shouldAddOneStudentToClasAndReturnThatClass() throws Exception {
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, "C24GV");
        Student student = new Student("Andreea", "Ioana", 15, clasa);
        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", List.of(student));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String expected = mapper.writeValueAsString(map);

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));
        when(jsonTransformMock.studentsToJson(classCode)).thenReturn(expected);
        MvcResult result = mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).save(argument.capture());
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddStudentToClassAndReturnError() throws Exception{
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, "C24GV");
        Student student = new Student("Andreea", "Ioana", 15, clasa);
        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", List.of(student));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String requestBody = "{\n" +
                "    \"firstName\": \"Andreea\",\n" +
                "    \"lastName\": \"Ioana\",\n" +
                "    \"age\": 15,\n" +
                "    \"classCode\": \"" + classCode + "\"\n" +
                "}";

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));
        when(jsonTransformMock.studentsToJson(classCode)).thenThrow(JsonProcessingException.class);
        MvcResult result = mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).save(argument.capture());
        assertEquals("ERROR", actual);
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

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(post("/rest/students/add").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(0)).save(argument.capture());  //we want to check that it entered on else branch, so it didn't save
        assertEquals("ERROR", actual);
    }

    @Test
    void shouldMoveStudentAndReturnTheStudentsInClassWhereMoved() throws Exception{
        String newClassCode = "C24GV";
        Class newClass = new Class("Filologie", 7, newClassCode);
        Class oldClass = new Class("Mate-Info", 7, "SDGF3");
        Student existingStudent = new Student("Andreea", "Ioana", 13, newClass);
        Student movedStudent = new Student("Ion", "Marcel", 14, oldClass);
        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", List.of(existingStudent, movedStudent));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String expected = mapper.writeValueAsString(map);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\",\n" +
                "     \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.of(movedStudent));
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.of(newClass));
        when(jsonTransformMock.studentsToJson(newClassCode)).thenReturn(expected);
        MvcResult result = mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).save(argument.capture());
        assertEquals(expected, actual);
    }

    @Test
    void shouldMoveStudentAndReturnError() throws Exception{
        String newClassCode = "C24GV";
        Class newClass = new Class("Filologie", 7, newClassCode);
        Class oldClass = new Class("Mate-Info", 7, "SDGF3");
        Student movedStudent = new Student("Ion", "Marcel", 14, oldClass);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\",\n" +
                "     \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.of(movedStudent));
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.of(newClass));
        when(jsonTransformMock.studentsToJson(newClassCode)).thenThrow(JsonProcessingException.class);
        MvcResult result = mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).save(argument.capture());
        assertEquals("ERROR", actual);
    }

    @Test
    void shouldNotMovStudentAndReturnError() throws Exception{
        String newClassCode = "C24GV";

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\",\n" +
                "     \"newClassCode\": \"" + newClassCode + "\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(patch("/rest/students/move").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(0)).save(argument.capture());
        assertEquals("ERROR", actual);
    }

    @Test
    void shouldDeleteStudentAndReturnOneRemainingStudentInThatClass() throws Exception{
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student remainingStudent = new Student("Andreea", "Ioana", 13, clasa);
        Student toDeleteStudent = new Student("Ion", "Marcel", 14, clasa);
        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", List.of(remainingStudent));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String expected = mapper.writeValueAsString(map);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.of(toDeleteStudent));
        when(jsonTransformMock.studentsToJson(classCode)).thenReturn(expected);
        MvcResult result = mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).delete(argument.capture());
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteStudentAndReturnError() throws Exception{
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student toDeleteStudent = new Student("Ion", "Marcel", 14, clasa);

        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.of(toDeleteStudent));
        when(jsonTransformMock.studentsToJson(classCode)).thenThrow(JsonProcessingException.class);
        MvcResult result = mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(1)).delete(argument.capture());
        assertEquals("ERROR", actual);
    }

    @Test
    void shouldNotDeleteStudentAndReturnError() throws Exception{
        String requestBody = "{\n" +
                "    \"firstName\": \"Ion\",\n" +
                "    \"lastName\": \"Marcel\"\n" +
                "}";

        when(studentsRepositoryMock.findByFirstNameAndLastName("Ion", "Marcel")).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(delete("/rest/students/delete").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andReturn();
        String actual = result.getResponse().getContentAsString();

        ArgumentCaptor<Student> argument = ArgumentCaptor.forClass(Student.class);
        verify(studentsRepositoryMock, times(0)).delete(argument.capture());
        assertEquals("ERROR", actual);
    }
}