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

    public String getStudentsByClass(String classCode) {
        return studentsRepository.findByClasaCode(classCode).toString();
    }

    public String addStudent(Map<String, String> body) {
        Optional<Class> clasa = classesRepository.findByCode(body.get("classCode"));

        if(clasa.isPresent()) {
            Student newStudent = new Student(body.get("firstName"), body.get("lastName"), Integer.parseInt(body.get("age")), clasa.get());
            studentsRepository.save(newStudent);
            return studentsRepository.findByClasaCode(body.get("classCode")).toString();
        }

        return "ERROR";
    }

    public String moveStudent(Map<String, String> body) {
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));
        Optional<Class> newClass = classesRepository.findByCode(body.get("newClassCode"));

        if(student.isPresent() && newClass.isPresent()) {
            Student updatedStudent = student.get();
            updatedStudent.setClasa(newClass.get());
            studentsRepository.save(updatedStudent);
            return studentsRepository.findByClasaCode(body.get("newClassCode")).toString();
        }

        return "ERROR";
    }

    public String deleteStudent(@RequestBody Map<String, String> body) {
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));

        if(student.isPresent()) {
            String classCode = student.get().getClasa().getCode();
            studentsRepository.delete(student.get());
            return studentsRepository.findByClasaCode(classCode).toString();
        }

        return "ERROR";
    }
}
