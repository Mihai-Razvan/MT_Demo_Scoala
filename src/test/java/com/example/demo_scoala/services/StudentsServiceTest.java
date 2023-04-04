package com.example.demo_scoala.services;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
class StudentsServiceTest {

    @MockBean
    StudentsRepository studentsRepositoryMock;
    @MockBean
    ClassesRepository classesRepositoryMock;
    @Autowired
    StudentsService studentsService;

    @Test
    void shouldReturnOneStudentToList() {   //testing getStudentsByClass
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student = new Student("Andrei", "Ionut",13, clasa);
        List<Student> studentList = List.of(student);

        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentList);

        List<Student> actual = studentsService.getStudentsByClass(classCode);
        assertEquals(studentList, actual);
    }

    @Test
    void shouldReturnTwoStudentsInOneClassToList() {  //testing getStudentsByClass
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student1 = new Student("Andrei", "Ionut",13, clasa);
        Student student2 = new Student("Marcel", "Nicolae",15, clasa);
        List<Student> studentList = new ArrayList<>(List.of(student1, student2));

        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentList);

        List<Student> actual = studentsService.getStudentsByClass(classCode);
        assertEquals(studentList, actual);
    }

    @Test
    void shouldReturnNoStudentInClassToList() {  //testing getStudentsByClass
        String classCode = "FAEAS5";

        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(new ArrayList<>());

        List<Student> actual = studentsService.getStudentsByClass(classCode);
        assertEquals(List.of(), actual);
    }

    @Test
    void shouldSaveStudentToClassAndReturnThatStudent() {  //testing addStudent
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student newStudent = new Student("Andreea", "Ioana", 15, clasa);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));

        Student actual = studentsService.addStudent(body);
        verify(studentsRepositoryMock, times(1)).save(any());
       // assertEquals(newStudent, actual);   NU MERGE????
        assertEquals(newStudent.getFirstName(), actual.getFirstName());
        assertEquals(newStudent.getLastName(), actual.getLastName());
        assertEquals(newStudent.getAge(), actual.getAge());
        assertEquals(newStudent.getClasa(), actual.getClasa());
    }

    @Test
    void shouldReturnNull() {  //testing addStudent
        String classCode = "C24GV";

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("age", "15");
        body.put("classCode", classCode);

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.empty());

        Student actual = studentsService.addStudent(body);
        verify(studentsRepositoryMock, times(0)).save(any());
        assertNull(actual);
    }

    @Test
    void shouldMoveStudentToClassAndReturnThatStudent() {  //testing moveStudent
        String oldClassCode = "C24GV";
        String newClassCode = "HE632H";
        Class oldClass = new Class("Filologie", 7, oldClassCode);
        Class newClass = new Class("Mate-Info", 7, newClassCode);
        Student student = new Student("Andreea", "Ioana", 15, oldClass);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"))).thenReturn(Optional.of(student));
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.of(newClass));

        Student actual = studentsService.moveStudent(body);
        verify(studentsRepositoryMock, times(1)).save(any());
        assertEquals(student.getFirstName(), actual.getFirstName());
        assertEquals(student.getLastName(), actual.getLastName());
        assertEquals(student.getAge(), actual.getAge());
        assertEquals(newClassCode, actual.getClasa().getCode());
    }

    @Test
    void shouldNotFindGivenStudentAndReturnNull() {  //testing moveStudent
        String newClassCode = "C24GV";
        Class clasa = new Class("Filologie", 7, newClassCode);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"))).thenReturn(Optional.empty());
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.of(clasa));

        Student actual = studentsService.moveStudent(body);
        verify(studentsRepositoryMock, times(0)).save(any());
        assertNull(actual);
    }

    @Test
    void shouldNotFindGivenClassAndReturnNull() {  //testing moveStudent
        String newClassCode = "C24GV";
        Class clasa = new Class("Filologie", 7, newClassCode);
        Student student = new Student("Andreea", "Ioana", 15, clasa);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");
        body.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"))).thenReturn(Optional.of(student));
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.empty());

        Student actual = studentsService.moveStudent(body);
        verify(studentsRepositoryMock, times(0)).save(any());
        assertNull(actual);
    }

    @Test
    void shouldDeleteStudentAndReturnThatStudent() {  //testing deleteStudent
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student deletedStudent = new Student("Andreea", "Ioana", 14, clasa);

        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");

        when(studentsRepositoryMock.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"))).thenReturn(Optional.of(deletedStudent));

        Student actual = studentsService.deleteStudent(body);
        verify(studentsRepositoryMock, times(1)).delete(any());
        assertEquals(deletedStudent, actual);
    }

    @Test
    void shouldNotDeleteStudentAndReturnNull() {  //testing deleteStudent
        Map<String, String> body = new HashMap<>();
        body.put("firstName", "Andreea");
        body.put("lastName", "Ioana");

        when(studentsRepositoryMock.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"))).thenReturn(Optional.empty());

        Student actual = studentsService.deleteStudent(body);
        verify(studentsRepositoryMock, times(0)).delete(any());
        assertNull(actual);
    }
}