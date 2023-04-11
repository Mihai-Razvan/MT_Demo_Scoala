package com.example.demo_scoala.services;

import com.example.demo_scoala.exceptions.NoFoundException;
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

    public List<Student> getStudentsByClassCode(String classCode) throws NoFoundException{
        Optional<Class> clasa = classesRepository.findByCode(classCode);
        if(clasa.isEmpty())
            throw new NoFoundException("Class not found!");

        return studentsRepository.findByClasaCode(classCode);
    }

    public Student addStudentToClass(Map<String, String> body) throws NoFoundException{
        Optional<Class> clasa = classesRepository.findByCode(body.get("classCode"));

        if(clasa.isPresent()) {
            Student newStudent = new Student(body.get("firstName"), body.get("lastName"), Integer.parseInt(body.get("age")), clasa.get());
            studentsRepository.save(newStudent);
            return newStudent;
        }

        throw new NoFoundException("Class not found!");
    }

    public Student moveStudentToOtherClass(Map<String, String> body) throws NoFoundException {
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));
        if(student.isEmpty())
            throw new NoFoundException("Student not found!");

        Optional<Class> newClass = classesRepository.findByCode(body.get("newClassCode"));
        if(newClass.isEmpty())
            throw new NoFoundException("Class not found!");

        Student updatedStudent = student.get();
        updatedStudent.setClasa(newClass.get());
        studentsRepository.save(updatedStudent);
        return updatedStudent;
    }

    public Student deleteStudent(@RequestBody Map<String, String> body) throws NoFoundException{
        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));

        if(student.isPresent()) {
            studentsRepository.delete(student.get());
            return student.get();
        }

        throw new NoFoundException("Student not found!");
    }
}
