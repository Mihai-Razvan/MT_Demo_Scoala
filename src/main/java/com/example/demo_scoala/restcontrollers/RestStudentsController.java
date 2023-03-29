package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.services.StudentsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rest/students")
public class RestStudentsController {

    private final StudentsService studentsService;

    public RestStudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/show")
    public String showStudentByClass(@RequestParam String classCode) {
        return studentsService.getStudentsByClass(classCode);
    }
    @PostMapping("/add")
    public String addStudent(@RequestBody Map<String, String> body) {  //adds a student to the given class (by code) and returns all the students in that class
        return studentsService.addStudent(body);
    }

    @PatchMapping("/move")
    public String moveStudent(@RequestBody Map<String, String> body) {   //move a student from a class to the given new class and returns all the students in that new class
        return studentsService.moveStudent(body);
    }

    @DeleteMapping("/delete")
    public String deleteStudent(@RequestBody Map<String, String> body) {  //given the firstName and lastName, deletes that student and returns all students in his former class
        return studentsService.deleteStudent(body);
    }
}