package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserCreatingOrUpdatingException;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin/api")
@CrossOrigin
public class AdminsController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminsController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(("/admin"))
    public ResponseEntity<List<User>> getAdminPage() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
    @GetMapping("/admin/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }
    @GetMapping("/authUser")
    public ResponseEntity<User> showAuthUser(Principal principal) {
        return new ResponseEntity<>(userService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> allRoles = roleService.getRoles();
        return new ResponseEntity<>(allRoles, HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user,
                                                 BindingResult bindingResult) {

        collectErrorMessage(bindingResult);
        userService.saveUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PatchMapping(value = "/admin/{id}")
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {

        collectErrorMessage(bindingResult);
        userService.updateUser(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/admin/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {

        userService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserCreatingOrUpdatingException exception) {
        UserErrorResponse response = new UserErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private void collectErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UserCreatingOrUpdatingException(errorMsg.toString());
        }
    }
}
