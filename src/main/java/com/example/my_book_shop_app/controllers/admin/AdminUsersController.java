package com.example.my_book_shop_app.controllers.admin;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminDeleteElementPayload;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminElementChangePayload;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.UserBooksService;
import com.example.my_book_shop_app.services.UserService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminUsersController {

    private final BookstoreUserRegister userRegister;
    private final UserService userService;
    private final BookService bookService;
    private final UserBooksService userBooksService;

    @Autowired
    public AdminUsersController(BookstoreUserRegister userRegister, UserService userService, BookService bookService, UserBooksService userBooksService) {
        this.userRegister = userRegister;
        this.userService = userService;
        this.bookService = bookService;
        this.userBooksService = userBooksService;
    }

    @ModelAttribute("currentRole")
    public String currentRole() {
        return userRegister.getCurrentUser().getRole().getName();
    }

    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return userBooksService.getBooksOfUser(userRegister.getCurrentUser().getId());
    }

    @ModelAttribute("userList")
    public List<UserEntity> userList() {
        return userService.getPageOfUsers(0, 20);
    }

    @GetMapping("/users")
    public String handelUsers(){
        return "users";
    }

    @GetMapping(value = "/users", params = {"offset", "limit"})
    public String handelUsersPage(@RequestParam("offset") Integer offset,
                                  @RequestParam("limit") Integer limit, RedirectAttributes attributes){
        attributes.addAttribute(userService.getPageOfUsers(offset, limit));
        return "redirect:/users";
    }

    @GetMapping("/users/{userId}")
    public String handelUserBooks(@PathVariable(name = "userId") int userId, Model model){
        model.addAttribute("booksData", userBooksService.getBooksOfUser(userId));
        model.addAttribute("userId", userId);
        model.addAttribute("userName", userService.getUserById(userId).getName());
        return "userBooks";
    }

    @PostMapping("/users/{userId}/books")
    @ResponseBody
    public ResultDto addBookToUser(@PathVariable(name = "userId") int userId, @RequestBody AdminElementChangePayload payload){
        String title = payload.getValue();
        if(bookService.isThereBookWithTitleIgnoreCase(title)) {
            Book book = bookService.getBookByTitleIgnoreCase(title);
            userBooksService.setBookAsPaid(userId, book.getId());
            return new ResultDto(true);
        } else return new ResultDto(false, "There is no book with such title");
    }

    @PostMapping("/users/{userId}/books/delete")
    @ResponseBody
    public ResultDto deleteBookFromUser(@PathVariable(name = "userId") int userId, @RequestBody AdminDeleteElementPayload payload){
        int bookId = payload.getElementId();
        userBooksService.removeBookFromPaid(userId, bookId);
        userBooksService.removeBookFromArchived(userId, bookId);
        return new ResultDto(true);
    }


}
