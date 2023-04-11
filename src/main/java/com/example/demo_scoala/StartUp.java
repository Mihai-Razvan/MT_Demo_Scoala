package com.example.demo_scoala;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.example.demo_scoala.repositories.TeachersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartUp implements CommandLineRunner {

    private final StudentsRepository studentsRepository;
    private final ClassesRepository classesRepository;
    private final TeachersRepository teachersRepository;

    public StartUp(StudentsRepository studentsRepository, ClassesRepository classesRepository, TeachersRepository teachersRepository) {
        this.studentsRepository = studentsRepository;
        this.classesRepository = classesRepository;
        this.teachersRepository = teachersRepository;
    }

    @Override
    public void run(String... args) {

        if(teachersRepository.count() != 0)
            return;

        Class clasa1 = new Class("Filologie", 11, "C24GV");
        Class clasa2 = new Class("Mate-Info", 10, "HE632H");
        classesRepository.saveAll(List.of(clasa1, clasa2));

        Student student1 = new Student("Ion", "Marcel", 12, clasa1);
        Student student2 = new Student("Ana", "Maria", 15, clasa1);
        Student student3 = new Student("Andrei", "Marian", 18, clasa2);

        Teacher teacher1 = new Teacher("ProfPrenume1", "ProfNume1", "Mate", List.of(clasa1, clasa2));
        Teacher teacher2 = new Teacher("ProfPrenume2", "ProfNume2", "Romana", List.of(clasa1));
        Teacher teacher3 = new Teacher("ProfPrenume3", "ProfNume3", "Info", List.of(clasa2));

        studentsRepository.saveAll(List.of(student1, student2, student3));
        teachersRepository.saveAll(List.of(teacher1, teacher2, teacher3));
    }
}
