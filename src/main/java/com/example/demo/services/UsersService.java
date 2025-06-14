package com.example.demo.services;

import com.example.demo.models.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public Page<Users> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    public List<Users> searchUsers(String searchTerm) {
        // Check if searchTerm is a number for age search
        Integer age = null;
        try {
            age = Integer.parseInt(searchTerm);
        } catch (NumberFormatException e) {
            // Ignore, searchTerm is not a number
        }
        return usersRepository.findBySearchTerm(searchTerm, age);
    }
}
