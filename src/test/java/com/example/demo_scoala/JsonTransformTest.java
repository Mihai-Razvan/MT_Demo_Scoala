package com.example.demo_scoala;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.example.demo_scoala.repositories.TeachersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
class JsonTransformTest {

   /* StudentsRepository studentsRepositoryMock;
    ClassesRepository classesRepositoryMock;
    TeachersRepository teachersRepositoryMock;
    JsonTransform jsonTransform;

    @BeforeEach
    void setUp() {
        studentsRepositoryMock = mock(StudentsRepository.class);
        classesRepositoryMock = mock(ClassesRepository.class);
        teachersRepositoryMock = mock(TeachersRepository.class);
        jsonTransform = new JsonTransform(studentsRepositoryMock, classesRepositoryMock, teachersRepositoryMock);
    }*/

    @MockBean
    StudentsRepository studentsRepositoryMock;
    @MockBean
    ClassesRepository classesRepositoryMock;
    @MockBean
    TeachersRepository teachersRepositoryMock;
    @Autowired
    JsonTransform jsonTransform;

    @BeforeEach
    void setUp() {
    //    jsonTransform = new JsonTransform(studentsRepositoryMock, classesRepositoryMock, teachersRepositoryMock);
    }


    @Test
    void studentsToJsonShouldReturnOneStudentToJson() throws JsonProcessingException {
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student1 = new Student("Andrei", "Ionut",13, clasa);
        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);

        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentList);
        String actual = jsonTransform.studentsToJson(classCode);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);

        JsonNode studentNode = node.get("students").get(0);
        assertEquals("Andrei", studentNode.get("firstName").textValue());
        assertEquals("Ionut", studentNode.get("lastName").textValue());
        assertEquals(13, studentNode.get("age").asInt());
        JsonNode clasaNode = studentNode.get("clasa");
        assertEquals("Mate-Info", clasaNode.get("profile").textValue());
        assertEquals(7, clasaNode.get("number").asInt());
        assertEquals("ADFS42", clasaNode.get("code").textValue());
    }

    @Test
    void studentsToJsonShouldReturnTwoStudentsInOneClassToJson() throws JsonProcessingException {
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Student student1 = new Student("Andrei", "Ionut",13, clasa);
        Student student2 = new Student("Marcel", "Nicolae",15, clasa);
        List<Student> studentList = new ArrayList<>(List.of(student1, student2));

        when(studentsRepositoryMock.findByClasaCode(classCode)).thenReturn(studentList);
        String actual = jsonTransform.studentsToJson(classCode);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);

        for(int i = 0; i < node.get("students").size(); i++) {
            JsonNode studentNode = node.get("students").get(i);
            assertEquals(studentList.get(i).getFirstName(), studentNode.get("firstName").textValue());
            assertEquals(studentList.get(i).getLastName(), studentNode.get("lastName").textValue());
            assertEquals(studentList.get(i).getAge(), studentNode.get("age").asInt());
            JsonNode clasaNode = studentNode.get("clasa");
            assertEquals(studentList.get(i).getClasa().getProfile(), clasaNode.get("profile").textValue());
            assertEquals(studentList.get(i).getClasa().getNumber(), clasaNode.get("number").asInt());
            assertEquals(studentList.get(i).getClasa().getCode(), clasaNode.get("code").textValue());
        }
    }

    @Test
    void studentsToJsonShouldReturnNoStudentInClassToJson() throws JsonProcessingException {
        when(studentsRepositoryMock.findByClasaCode("FAEAS5")).thenReturn(new ArrayList<>());
        String actual = jsonTransform.studentsToJson("FAEAS5");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);
        assertTrue(node.get("students").isEmpty());
    }

    @Test
    void teachersToJsonShouldReturnOneTeacherWithNoClassesToJson() throws JsonProcessingException {
        String classCode = "ADFS42";
        Class clasa = new Class("Mate-Info", 7, classCode);
        Teacher teacher = new Teacher("ProfPrenume1", "ProfNume1", "Matematica", List.of(clasa));
        List<Teacher> teacherList = new ArrayList<>(List.of(teacher));

        when(teachersRepositoryMock.findAll()).thenReturn(teacherList);
        String actual = jsonTransform.teachersToJson();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);

        for(int i = 0; i < node.get("teachers").size(); i++) {
            JsonNode teacherNode = node.get("teachers").get(i);
            assertEquals(teacherList.get(i).getFirstName(), teacherNode.get("firstName").textValue());
            assertEquals(teacherList.get(i).getLastName(), teacherNode.get("lastName").textValue());
            assertEquals(teacherList.get(i).getSubject(), teacherNode.get("subject").textValue());

            for(int j = 0; j < teacherNode.get("classes").size(); j++) {
                JsonNode clasaNode = teacherNode.get("classes").get(j);
                assertEquals(teacherList.get(i).getClasses().get(j).getProfile(), clasaNode.get("profile").textValue());
                assertEquals(teacherList.get(i).getClasses().get(j).getNumber(), clasaNode.get("number").asInt());
                assertEquals(teacherList.get(i).getClasses().get(j).getCode(), clasaNode.get("code").textValue());
            }
        }
    }

    @Test
    void teachersToJsonShouldReturnTwoTeachersToJson() throws JsonProcessingException {
        String classCode1 = "ADFS42";
        String classCode2 = "GSE42G";
        Class clasa1 = new Class("Mate-Info", 7, classCode1);
        Class clasa2 = new Class("Filologie", 10, classCode2);
        Teacher teacher1 = new Teacher("ProfPrenume1", "ProfNume1", "Matematica", List.of(clasa1));
        Teacher teacher2 = new Teacher("ProfPrenume2", "ProfNume2", "Romana", List.of(clasa1, clasa2));
        List<Teacher> teacherList = new ArrayList<>(List.of(teacher1, teacher2));

        when(teachersRepositoryMock.findAll()).thenReturn(teacherList);
        String actual = jsonTransform.teachersToJson();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);

        for(int i = 0; i < node.get("teachers").size(); i++) {
            JsonNode teacherNode = node.get("teachers").get(i);
            assertEquals(teacherList.get(i).getFirstName(), teacherNode.get("firstName").textValue());
            assertEquals(teacherList.get(i).getLastName(), teacherNode.get("lastName").textValue());
            assertEquals(teacherList.get(i).getSubject(), teacherNode.get("subject").textValue());

            for(int j = 0; j < teacherNode.get("classes").size(); j++) {
                JsonNode clasaNode = teacherNode.get("classes").get(j);
                assertEquals(teacherList.get(i).getClasses().get(j).getProfile(), clasaNode.get("profile").textValue());
                assertEquals(teacherList.get(i).getClasses().get(j).getNumber(), clasaNode.get("number").asInt());
                assertEquals(teacherList.get(i).getClasses().get(j).getCode(), clasaNode.get("code").textValue());
            }
        }
    }

    @Test
    void teachersToJsonShouldReturnNoTeacherToJson() throws JsonProcessingException {
        when(teachersRepositoryMock.findAll()).thenReturn(new ArrayList<>());
        String actual = jsonTransform.teachersToJson();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(actual);
        assertTrue(node.get("teachers").isEmpty());
    }
}