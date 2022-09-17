package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserEntity> getAllUsers() {
        return this.userRepository.findAll();
    }

    public UserEntity getUserById(int userId) {
        return this.userRepository.findUserEntityById(userId);
    }

    public List<UserEntity> getPageOfUsers(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return this.userRepository.findAll(nextPage).getContent();
    }

    public List<UserEntity> getUsersBySearchWord(String searchWord) {
        return this.userRepository.findUserEntitiesByNameContainingIgnoreCase(searchWord);
    }
}
