package com.example.demo.repository;

import com.example.demo.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends MongoRepository<Users, String> {

    @Query("{'$or': [{'name': {'$regex': ?0, '$options': 'i'}}, {'surname': {'$regex': ?0, '$options': 'i'}}, {'age': ?1}]}")
    List<Users> findBySearchTerm(String searchTerm, Integer age);
}
