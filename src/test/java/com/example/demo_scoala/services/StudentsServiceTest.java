package com.example.demo_scoala.services;

import com.example.demo_scoala.exceptions.NoFoundException;
import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
    void shouldReturnOneStudentToList() throws NoFoundException{   //testing getStudentsByClass
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student = new Student("Andrei", "Ionut",13, clasa);
        List<Student> studentsInClass = List.of(student);

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));
        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentsInClass);

        List<Student> actual = studentsService.getStudentsByClassCode(classCode);
        assertEquals(studentsInClass, actual);
    }

    @Test
    void shouldReturnTwoStudentsInOneClassToList() throws NoFoundException{  //testing getStudentsByClass
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student1 = new Student("Andrei", "Ionut",13, clasa);
        Student student2 = new Student("Marcel", "Nicolae",15, clasa);
        List<Student> studentsInClass = new ArrayList<>(List.of(student1, student2));

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));
        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentsInClass);

        List<Student> actual = studentsService.getStudentsByClassCode(classCode);
        assertEquals(studentsInClass, actual);
    }

    @Test
    void shouldThrowNoFoundExceptionWhenShowingClass() {  //testing getStudentsByClass
        String classCode = "FAEAS5";

        when(classesRepositoryMock.findByCode(classCode)).thenThrow(NoFoundException.class);

        assertThrows(NoFoundException.class, () -> {
            studentsService.getStudentsByClassCode(classCode);
        });
    }

    @Test
    void shouldSaveStudentToClassAndReturnThatStudent() throws NoFoundException{  //testing addStudent
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student newStudent = new Student("Andreea", "Ioana", 15, clasa);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");
        requestBody.put("age", "15");
        requestBody.put("classCode", classCode);

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.of(clasa));

        Student actual = studentsService.addStudentToClass(requestBody);
        verify(studentsRepositoryMock, times(1)).save(any());
        assertEquals(newStudent.getFirstName(), actual.getFirstName());
        assertEquals(newStudent.getLastName(), actual.getLastName());
        assertEquals(newStudent.getAge(), actual.getAge());
        assertEquals(newStudent.getClasa(), actual.getClasa());
    }

    @Test
    void shouldThrowNoFoundExceptionWhenAddingStudent() throws NoFoundException {  //testing addStudent
        String classCode = "C24GV";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");
        requestBody.put("age", "15");
        requestBody.put("classCode", classCode);

        when(classesRepositoryMock.findByCode(classCode)).thenReturn(Optional.empty());

        assertThrows(NoFoundException.class, () -> {
            studentsService.addStudentToClass(requestBody);
        });
    }

    @Test
    void shouldMoveStudentToClassAndReturnThatStudent() throws NoFoundException{  //testing moveStudent
        String oldClassCode = "C24GV";
        String newClassCode = "HE632H";
        Class oldClass = new Class("Filologie", 7, oldClassCode);
        Class newClass = new Class("Mate-Info", 7, newClassCode);
        Student student = new Student("Andreea", "Ioana", 15, oldClass);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");
        requestBody.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(requestBody.get("firstName"), requestBody.get("lastName"))).thenReturn(Optional.of(student));
        when(classesRepositoryMock.findByCode(newClassCode)).thenReturn(Optional.of(newClass));

        Student actual = studentsService.moveStudentToOtherClass(requestBody);
        verify(studentsRepositoryMock, times(1)).save(any());
        assertEquals(student.getFirstName(), actual.getFirstName());
        assertEquals(student.getLastName(), actual.getLastName());
        assertEquals(student.getAge(), actual.getAge());
        assertEquals(newClassCode, actual.getClasa().getCode());
    }

    @Test
    void shouldThrowNotFoundExceptionBecauseStudentToBeMovedNotFound() throws NoFoundException{  //testing moveStudent
        String newClassCode = "C24GV";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");
        requestBody.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(requestBody.get("firstName"), requestBody.get("lastName"))).thenReturn(Optional.empty());

        assertThrows(NoFoundException.class, () -> {
            studentsService.moveStudentToOtherClass(requestBody);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionBecauseNewClassNotFound() throws NoFoundException{  //testing moveStudent
        String newClassCode = "C24GV";
        Class clasa = new Class("Filologie", 7, newClassCode);
        Student student = new Student("Andreea", "Ioana", 15, clasa);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");
        requestBody.put("newClassCode", newClassCode);

        when(studentsRepositoryMock.findByFirstNameAndLastName(requestBody.get("firstName"), requestBody.get("lastName"))).thenReturn(Optional.of(student));

        assertThrows(NoFoundException.class, () -> {
            studentsService.moveStudentToOtherClass(requestBody);
        });
    }

    @Test
    void shouldDeleteStudentAndReturnThatStudent() throws NoFoundException{  //testing deleteStudent
        String classCode = "C24GV";
        Class clasa = new Class("Filologie", 7, classCode);
        Student deletedStudent = new Student("Andreea", "Ioana", 14, clasa);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");

        when(studentsRepositoryMock.findByFirstNameAndLastName(requestBody.get("firstName"), requestBody.get("lastName"))).thenReturn(Optional.of(deletedStudent));

        Student actual = studentsService.deleteStudent(requestBody);
        verify(studentsRepositoryMock, times(1)).delete(any());
        assertEquals(deletedStudent, actual);
    }

    @Test
    void shouldNotDeleteStudentAndReturnNull() throws NoFoundException{  //testing deleteStudent
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstName", "Andreea");
        requestBody.put("lastName", "Ioana");

        when(studentsRepositoryMock.findByFirstNameAndLastName(requestBody.get("firstName"), requestBody.get("lastName"))).thenReturn(Optional.empty());

        assertThrows(NoFoundException.class, () -> {
            studentsService.deleteStudent(requestBody);
        });
    }
}