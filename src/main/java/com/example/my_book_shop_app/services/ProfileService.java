package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.exceptions.LowUserBalanceException;
import com.example.my_book_shop_app.repositories.UserContactRepository;
import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.struct.user.UserContactEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    public ProfileService(UserRepository userRepository, UserContactRepository userContactRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
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

    public UserEntity increaseUserBalance(UserEntity user, Double sum) {
        user.setBalance(user.getBalance() + sum);
        logger.info("User's ({}) balance has been increased to {}", user.getName(), user.getBalance());
        return userRepository.save(user);
    }

    public UserEntity reduceUserBalance(UserEntity user, Double sum) throws LowUserBalanceException {
        Double currentBalance = user.getBalance();
        if (currentBalance < sum) throw new LowUserBalanceException(String.format("Balance of user %s is %.2f, that lower than %.2f, that is total price of books", user.getName(), currentBalance, sum));
        user.setBalance(currentBalance - sum);
        logger.info("User's ({}) balance has been reduced to {}", user.getName(), user.getBalance());
        return userRepository.save(user);
    }

    public void changeMail(UserEntity user, String mail) {
        UserContactEntity contact = user.getEmailContact();
        if(!contact.getContact().equals(mail)) {
            contact.setContact(mail);
            userContactRepository.save(contact);
            logger.info("User mail has been changed");
        } else {
            logger.info("User mail has NOT been changed. New mail equals old mail");
        }
    }

    public void changePhone(UserEntity user, String phone) {
        UserContactEntity contact = user.getPhoneContact();
        if(!contact.getContact().equals(phone)) {
            contact.setContact(phone);
            userContactRepository.save(contact);
            logger.info("User phone has been changed");
        } else {
            logger.info("User phone has NOT been changed. New phone equals old phone");
        }
    }

    public void changeName(UserEntity user, String name) {
        if (!user.getName().equals(name)) {
            user.setName(name);
            userRepository.save(user);
            logger.info("User name has been changed");
        } else {
            logger.info("User name has NOT been changed. New name is not new");
        }
    }
}
