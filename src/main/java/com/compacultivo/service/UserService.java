package com.compacultivo.service;

import com.compacultivo.Entity.Role;
import com.compacultivo.Entity.SubscriptionStatus;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User activate(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(true);
        user.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        user.setSubscriptionEndDate(LocalDateTime.now().plusDays(30));
        return userRepository.save(user);
    }

    public User deactivate(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(false);
        user.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        return userRepository.save(user);
    }

    public User changeRole(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(role);
        return userRepository.save(user);
    }

    public User activateByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return activate(user.getId());
    }
}
