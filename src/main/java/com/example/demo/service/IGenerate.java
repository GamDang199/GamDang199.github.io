package com.example.demo.service;

import java.util.List;
import java.util.Optional;

public interface IGenerate<T> {
    List<T> findAll();
    Optional<T> findById(int id);
    void save(T t);
    void delete(int id);
}
