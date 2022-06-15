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
        registerNewUser(registrationForm.getEmail(), registrationForm.getName(), registrationForm.getPassword());
    }

    public void registerNewUser(DefaultOAuth2User oAuth2User) throws UserAlreadyExistException {
        Map<String, Object> attrs = oAuth2User.getAttributes();
        registerNewUser(attrs.get("email").toString(), attrs.get("name").toString(), attrs.get("id").toString());
    }

    private void registerNewUser(String email, String name, String password) throws UserAlreadyExistException {
        if(userContactRepository.findUserContactEntityByContact(email) == null) {
            UserEntity user = new UserEntity();
            user.setName(name);
            user.setRegTime(LocalDateTime.now());
            user.setHash("hash");
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            UserContactEntity contact = new UserContactEntity();
            contact.setType(ContactType.EMAIL);
            contact.setApproved((short) 1);
            contact.setUserId(user.getId());
            contact.setCode("code");
            contact.setCodeTime(LocalDateTime.now());
            contact.setCodeTrails(1);
            contact.setContact(email);
            userContactRepository.save(contact);
        } else throw new UserAlreadyExistException("User with email " + email + " is already signed up");
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ContactConfirmationResponse(true);
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByEmail(payload.getContact());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getUsername(), payload.getCode()));
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

}
