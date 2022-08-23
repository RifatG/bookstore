package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.data.ContactConfirmationPayload;
import com.example.my_book_shop_app.data.ContactConfirmationResponse;
import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import com.example.my_book_shop_app.repositories.UserContactRepository;
import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.security.jwt.JWTUtil;
import com.example.my_book_shop_app.struct.enums.ContactType;
import com.example.my_book_shop_app.struct.user.UserContactEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookstoreUserRegister {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public BookstoreUserRegister(UserRepository userRepository, UserContactRepository userContactRepository, BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public void registerNewUser(RegistrationForm registrationForm) throws UserAlreadyExistException {
        registerNewUser(registrationForm.getEmail(), registrationForm.getName(), registrationForm.getPhoneNumber(), registrationForm.getPassword());
    }

    public void registerNewUser(DefaultOAuth2User oAuth2User) throws UserAlreadyExistException {
        Map<String, Object> attrs = oAuth2User.getAttributes();
        registerNewUser(attrs.get("email").toString(), attrs.get("name").toString(), attrs.get("phone").toString(), attrs.get("id").toString());
    }

    private void registerNewUser(String email, String name, String phoneNumber, String password) throws UserAlreadyExistException {
        if(userContactRepository.findUserContactEntityByContact(email) == null) {
            UserEntity user = new UserEntity();
            user.setName(name);
            user.setRegTime(LocalDateTime.now());
            user.setHash("hash");
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            createUserContactsForUser(ContactType.EMAIL, user.getId(), email);
            createUserContactsForUser(ContactType.PHONE, user.getId(), phoneNumber);
        } else throw new UserAlreadyExistException("User with email " + email + " is already signed up");
    }

    private void createUserContactsForUser(ContactType contactType, Integer userId, String contact) {
        UserContactEntity userContact = new UserContactEntity();
        userContact.setType(contactType);
        userContact.setApproved((short) 1);
        userContact.setUserId(userId);
        userContact.setCode("code");
        userContact.setCodeTime(LocalDateTime.now());
        userContact.setCodeTrails(0);
        userContact.setContact(contact);
        userContactRepository.save(userContact);
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ContactConfirmationResponse(true);
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByContact(payload.getContact());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getUsername(), payload.getCode()));
        String jwtToken = jwtUtil.generateToken(userDetails);
        return new ContactConfirmationResponse(true, jwtToken);
    }

    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload) {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByContact(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        return new ContactConfirmationResponse(true, jwtToken);
    }
    
    public UserEntity getCurrentUser() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) userDetails;
            String username = oAuth2User.getAttributes().get("name").toString();
            return this.userRepository.findUserEntityByName(username);
        }
        if(userDetails instanceof BookstoreUserDetails) {
            BookstoreUserDetails bookstoreUserDetails = (BookstoreUserDetails) userDetails;
            return bookstoreUserDetails.getBookstoreUser();
        }
        return null;
    }

    public boolean isAuthenticated() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (user instanceof DefaultOAuth2User || user instanceof BookstoreUserDetails);
    }
}
