package com.example.furniture.service;

import com.example.furniture.entity.User;
import com.example.furniture.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: {}", username);
        final User u = userRepository.findByUsername(username)
                                     .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return toUserDetails(u);
    }

    public static UserDetails toUserDetails(final User u) {
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), List.of());
    }
}
