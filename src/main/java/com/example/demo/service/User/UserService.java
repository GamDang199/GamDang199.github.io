package com.example.demo.service.User;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{
    @Autowired
    UserRepository userRepository;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        Optional<User> user = Optional.ofNullable(findByUsername(username));
        if(user.isPresent() && user.get().getUsername().equals(username)){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPassword(String p1, String p2) {
        if(p1.equals(p2)){
            return true;
        }
        return false;
    }

    @Override
    public int checkLogin(String username, String password) {
        Optional<User> user = Optional.ofNullable(findByUsername(username));
        if(user.isPresent() && user.get().getUsername().equals(username) && user.get().getPassword().equals(password)){
            return user.get().getId();
        }
        return 0;
    }
}