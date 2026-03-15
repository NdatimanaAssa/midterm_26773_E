package com.quickbuild.controller;

import com.quickbuild.domain.User;
import com.quickbuild.dto.request.UserRegistrationRequest;
import com.quickbuild.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
    }

    // Additional helpful endpoints
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{type}/{code}")
    public ResponseEntity<List<User>> getUsersByLocation(
            @PathVariable("type") String type,
            @PathVariable("code") String code) {
        return ResponseEntity.ok(userService.getUsersByLocation(type, code));
    }
}
