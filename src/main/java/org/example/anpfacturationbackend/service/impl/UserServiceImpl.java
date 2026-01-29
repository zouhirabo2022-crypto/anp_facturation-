package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.UserDTO;
import org.example.anpfacturationbackend.entity.Role;
import org.example.anpfacturationbackend.entity.User;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.repository.RoleRepository;
import org.example.anpfacturationbackend.repository.UserRepository;
import org.example.anpfacturationbackend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByUsername(String username) {
        return userRepository.findById(username)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public UserDTO create(UserDTO dto) {
        if (userRepository.existsById(dto.getUsername())) {
            throw new IllegalArgumentException("User already exists: " + dto.getUsername());
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user.setRoles(mapRoles(dto.getRoles()));
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDTO update(String username, UserDTO dto) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setEnabled(dto.isEnabled());
        user.setRoles(mapRoles(dto.getRoles()));
        return toDto(userRepository.save(user));
    }

    @Override
    public void delete(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void toggleStatus(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }

    private Set<Role> mapRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            for (String name : roleNames) {
                roleRepository.findByName(name).ifPresent(roles::add);
            }
        }
        return roles;
    }
}
