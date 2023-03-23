package com.example.demo_scoala.repositories;

import com.example.demo_scoala.models.Class;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassesRepository extends CrudRepository<Class, Long> {

    Optional<Class> findByCode(String code);
}
