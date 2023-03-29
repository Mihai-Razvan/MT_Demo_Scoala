package com.example.demo_scoala.services;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.example.demo_scoala.repositories.TeachersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TeachersServiceTest {

    @MockBean
    TeachersRepository teachersRepositoryMock;
    @Autowired
    TeachersService teachersService;

    @Test
    void shouldReturnTwoTeachers() {
        Class clasa1 = new Class("Mate-Info", 7, "ASD432");
        Class clasa2 = new Class("Filologie", 11, "HSRE2R");
        Teacher teacher1 = new Teacher("Prenume1", "Nume1", "Biologie", List.of(clasa1));
        Teacher teacher2 = new Teacher("Prenume2", "Nume2", "Chimie", List.of(clasa1, clasa2));

        when(teachersRepositoryMock.findAll()).thenReturn(List.of(teacher1, teacher2));
        String actual = teachersService.getTeachers();
        assertEquals(List.of(teacher1, teacher2).toString(), actual);
    }

    @Test
    void shouldReturnEmptyList() {
        when(teachersRepositoryMock.findAll()).thenReturn(List.of());

        String actual = teachersService.getTeachers();
        assertEquals(List.of().toString(), actual);
    }
}