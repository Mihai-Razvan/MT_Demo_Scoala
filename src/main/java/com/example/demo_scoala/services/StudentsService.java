package com.example.demo_scoala.services;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class StudentsService {

    private final StudentsRepository studentsRepository;
    private final ClassesRepository classesRepository;

    public StudentsService(StudentsRepository studentsRepository, ClassesRepository classesRepository) {
        this.studentsRepository = studentsRepository;
        this.classesRepository = classesRepository;
    }

    public List<Student> getStudentsByClass(String classCode) {
        return studentsRepository.findByClasaCode(classCode);
    }

    public Student addStudent(Map<String, String> body) {
        Optional<Class> clasa = classesRepository.findByCode(body.get("classCode"));

        if(clasa.isPresent()) {
            Student newStudent = new Student(body.get("firstName"), body.get("lastName"), Integer.parseInt(body.get("age")), clasa.get());
            studentsRepository.save(newStudent);
            return newStudent;
        }

        return null;
    }

    public Student moveStudent(Map<String, String> body) {
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));
        Optional<Class> newClass = classesRepository.findByCode(body.get("newClassCode"));

        if(student.isPresent() && newClass.isPresent()) {
            Student updatedStudent = student.get();
            updatedStudent.setClasa(newClass.get());
            studentsRepository.save(updatedStudent);
            return updatedStudent;
        }

        return null;
    }

    public Student deleteStudent(@RequestBody Map<String, String> body) {
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));

        if(student.isPresent()) {
            studentsRepository.delete(student.get());
            return student.get();
        }

        return null;
    }
}
