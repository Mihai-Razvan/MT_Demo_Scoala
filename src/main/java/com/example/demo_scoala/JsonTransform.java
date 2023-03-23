package com.example.demo_scoala;

import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.example.demo_scoala.repositories.TeachersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JsonTransform {

    private final StudentsRepository studentsRepository;
    private final ClassesRepository classesRepository;

    private final TeachersRepository teachersRepository;

    public JsonTransform(StudentsRepository studentsRepository, ClassesRepository classesRepository, TeachersRepository teachersRepository) {
        this.studentsRepository = studentsRepository;
        this.classesRepository = classesRepository;
        this.teachersRepository = teachersRepository;
    }

    public String studentsToJson(String classCode) throws JsonProcessingException{    //returns the students from a given class as a json

        Map<String, List<Student>> map = new HashMap<>();
        map.put("students", studentsRepository.findByClasaCode(classCode));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(map);
    }

    public String teachersToJson() throws JsonProcessingException{       //returns all the teachers as a json

        List<Teacher> teacherList = new ArrayList<>();
        teachersRepository.findAll().forEach(teacherList::add);

        Map<String, List<Teacher>> map = new HashMap<>();
        map.put("teachers", teacherList);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(map);
    }
}
