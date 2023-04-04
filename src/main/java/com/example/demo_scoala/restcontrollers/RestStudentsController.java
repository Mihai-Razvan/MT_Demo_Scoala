package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.services.StudentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/students")
public class RestStudentsController {

    private final StudentsService studentsService;

    public RestStudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/show")
    public List<Student> showStudentByClass(@RequestParam String classCode) {
        return studentsService.getStudentsByClass(classCode);
    }
    @PostMapping("/add")
    public Student addStudent(@RequestBody Map<String, String> body) {  //adds a student to the given class (by code) and returns that student
        return studentsService.addStudent(body);
    }

    @PatchMapping("/move")
    public Student moveStudent(@RequestBody Map<String, String> body) {   //move a student from a class to the given new class and returns that student
        return studentsService.moveStudent(body);
    }

    @DeleteMapping("/delete")
    public Student deleteStudent(@RequestBody Map<String, String> body) {  //given the firstName and lastName, deletes that student and returns it
        return studentsService.deleteStudent(body);
    }
}