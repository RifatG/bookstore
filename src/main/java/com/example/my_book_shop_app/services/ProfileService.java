package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(UserEntity user, String newPassword) {
        if (newPassword != null && !newPassword.equals("")) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("User password has been changed");
        } else {
            logger.info("User password has NOT been changed. New password is empty");
        }
    }

    public void topUpUserBalance(UserEntity user, Double sum) {
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
        logger.info("User balance has been increased to {}", user.getBalance());
    }
}
