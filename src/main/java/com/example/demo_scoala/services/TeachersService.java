package com.example.demo_scoala.services;

import com.example.demo_scoala.repositories.TeachersRepository;
import org.springframework.stereotype.Service;

@Service
public class TeachersService {

    TeachersRepository teachersRepository;

    public TeachersService(TeachersRepository teachersRepository) {
        this.teachersRepository = teachersRepository;
    }

    public String getTeachers() {
        return teachersRepository.findAll().toString();
    }
}
