package com.example.demo_scoala.services;

import com.example.demo_scoala.models.Teacher;
import com.example.demo_scoala.repositories.TeachersRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TeachersService {

    TeachersRepository teachersRepository;

    public TeachersService(TeachersRepository teachersRepository) {
        this.teachersRepository = teachersRepository;
    }

    public List<Teacher> getTeachers() {
        ArrayList<Teacher> teacherList = new ArrayList<>();

        for(Teacher teacher : teachersRepository.findAll())
            teacherList.add(teacher);

        return teacherList;
    }
}
