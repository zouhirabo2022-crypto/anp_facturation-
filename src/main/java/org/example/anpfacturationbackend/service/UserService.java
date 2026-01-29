package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> findAll();

    UserDTO findByUsername(String username);

    UserDTO create(UserDTO dto);

    UserDTO update(String username, UserDTO dto);

    void delete(String username);

    void toggleStatus(String username);
}
