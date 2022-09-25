package com.example.my_book_shop_app.unit_tests.security_tests;

import com.example.my_book_shop_app.data.ContactConfirmationPayload;
import com.example.my_book_shop_app.data.ContactConfirmationResponse;
import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import com.example.my_book_shop_app.repositories.User2RoleRepository;
import com.example.my_book_shop_app.repositories.UserContactRepository;
import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.security.RegistrationForm;
import com.example.my_book_shop_app.struct.user.User2Role;
import com.example.my_book_shop_app.struct.user.UserContactEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository bookstoreUserRepositoryMock;

    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @MockBean
    private User2RoleRepository user2RoleRepository;

    @MockBean
    private AuthenticationManager authenticationManagerMock;

    @MockBean
    private Authentication authenticationMock;

    @MockBean
    private SecurityContext securityContextMock;

    @Autowired
    public BookstoreUserRegisterTests(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPhoneNumber("9031232323");
        registrationForm.setPassword("123123");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    void registerNewUser() throws UserAlreadyExistException {
        userRegister.registerNewUser(registrationForm);

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(2))
                .save(Mockito.any(UserContactEntity.class));
        Mockito.verify(user2RoleRepository, Mockito.times(1))
                .save(Mockito.any(User2Role.class));
    }

    @Test
    void registerNewOAuth2User() throws UserAlreadyExistException {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "1234");
        attributes.put("name", "Tester");
        attributes.put("email", "test@test.test");
        attributes.put("phone", "79999999999");
        DefaultOAuth2User user = new DefaultOAuth2User(authorities, attributes, "id");

        userRegister.registerNewUser(user);

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(2))
                .save(Mockito.any(UserContactEntity.class));
        Mockito.verify(user2RoleRepository, Mockito.times(1))
                .save(Mockito.any(User2Role.class));
    }

    @Test
    void registerNewUserFail(){
        Mockito.doReturn(new UserContactEntity())
                .when(userContactRepositoryMock)
                .findUserContactEntityByContact(registrationForm.getEmail());

        assertThrows(UserAlreadyExistException.class, () -> userRegister.registerNewUser(registrationForm));
        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(0))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(0))
                .save(Mockito.any(UserContactEntity.class));
    }

    @Test
    void jwtLoginTest() {
        UserContactEntity userContact = new UserContactEntity();
        userContact.setUserId(2);
        UserEntity user = new UserEntity();
        user.setId(2);

        Mockito
                .doReturn(userContact)
                .when(userContactRepositoryMock)
                .findUserContactEntityByContact(Mockito.any(String.class));
        Mockito
                .doReturn(user)
                .when(bookstoreUserRepositoryMock)
                .findUserEntityById(Mockito.any(Integer.class));

        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact("test@test.test");
        ContactConfirmationResponse response = userRegister.jwtLogin(payload);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getResult());

        Mockito
                .verify(userContactRepositoryMock, Mockito.times(1))
                .findUserContactEntityByContact(Mockito.any(String.class));
        Mockito
                .verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .findUserEntityById(Mockito.any(Integer.class));
        Mockito
                .verify(authenticationManagerMock, Mockito.times(1))
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void jwtLoginFailTest() {
        Mockito
                .doReturn(null)
                .when(userContactRepositoryMock)
                .findUserContactEntityByContact(Mockito.any(String.class));

        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        assertThrows(UsernameNotFoundException.class, () -> userRegister.jwtLogin(payload));

        Mockito
                .verify(userContactRepositoryMock, Mockito.times(1))
                .findUserContactEntityByContact(null);
        Mockito
                .verify(bookstoreUserRepositoryMock, Mockito.times(0))
                .findUserEntityById(Mockito.any(Integer.class));
        Mockito
                .verify(authenticationManagerMock, Mockito.times(0))
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void getCurrentUserTest() {
        UserEntity user = new UserEntity();
        user.setId(2);
        BookstoreUserDetails userDetails = new BookstoreUserDetails(user);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.doReturn(authenticationMock).when(securityContextMock).getAuthentication();
        Mockito.doReturn(userDetails).when(authenticationMock).getPrincipal();

        assertEquals(user, userRegister.getCurrentUser());

        Mockito
                .verify(securityContextMock, Mockito.times(1))
                .getAuthentication();
        Mockito
                .verify(authenticationMock, Mockito.times(1))
                .getPrincipal();
    }

    @Test
    void getCurrentUserAsOAuthTest() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Tester");
        attributes.put("id", "1234");
        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "id");
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.doReturn(authenticationMock).when(securityContextMock).getAuthentication();
        Mockito.doReturn(oAuth2User).when(authenticationMock).getPrincipal();
        Mockito.doReturn(new UserEntity()).when(bookstoreUserRepositoryMock).findUserEntityByName(Mockito.any(String.class));

        assertNotNull(userRegister.getCurrentUser());

        Mockito
                .verify(securityContextMock, Mockito.times(1))
                .getAuthentication();
        Mockito
                .verify(authenticationMock, Mockito.times(1))
                .getPrincipal();
        Mockito
                .verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .findUserEntityByName(Mockito.any(String.class));
    }
}
