package com.example.usersservice.service;

import com.example.usersservice.dtos.UserCreateDTO;
import com.example.usersservice.dtos.UserDTO;
import com.example.usersservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.usersservice.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static UserDTO toDto(User user) {
        return new  UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserService::toDto)
                .toList();
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(UserService::toDto);
    }

    public UserDTO createUser(UserCreateDTO dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .build();
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public Optional<UserDTO> updateUser(Long id, UserCreateDTO dto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(dto.getFirstName());
                    user.setLastName(dto.getLastName());
                    user.setEmail(dto.getEmail());
                    return toDto(userRepository.save(user));
                });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
