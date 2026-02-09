package org.anpfacturationbackend.controller;

import org.anpfacturationbackend.dto.UserDTO;
import org.anpfacturationbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN_SYSTEME')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{username}")
    public UserDTO getOne(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/{username}")
    public UserDTO update(@PathVariable String username, @RequestBody UserDTO dto) {
        return userService.update(username, dto);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable String username) {
        userService.toggleStatus(username);
        return ResponseEntity.ok().build();
    }
}

