package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.repositories.UserContactRepository;
import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.struct.user.UserContactEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    public BookstoreUserDetailsService(UserRepository userRepository, UserContactRepository userContactRepository) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity bookstoreUser = userRepository.findUserEntityByName(username);
        if (bookstoreUser != null) {
            return new BookstoreUserDetails(bookstoreUser);
        } else {
            throw new UsernameNotFoundException("User not found ");
        }
    }

    public UserDetails loadUserByContact(String contact) throws UsernameNotFoundException {
        UserContactEntity bookstoreUserContact = userContactRepository.findUserContactEntityByContact(contact);
        if (bookstoreUserContact != null) {
            UserEntity bookstoreUser = userRepository.findUserEntityById(bookstoreUserContact.getUserId());
            return new BookstoreUserDetails(bookstoreUser);
        } else {
            throw new UsernameNotFoundException("User's email not found ");
        }
    }
}
