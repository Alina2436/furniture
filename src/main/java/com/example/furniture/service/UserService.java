package com.example.furniture.service;

import com.example.furniture.dto.ProfileDto;
import com.example.furniture.dto.RegisterDto;
import com.example.furniture.entity.User;
import com.example.furniture.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialNotFoundException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Long findIdByUsername(final String username) {

        return userRepository.findByUsername(username)
                             .map(User::getId)
                             .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User findByUsernameFetch(final String username) {

        return userRepository.findByUsernameFetch(username)
                             .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public void save(final User user) {
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(final Long id, final ProfileDto dto) {

        final User user = userRepository.findById(id).orElseThrow();

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setPatronymic(dto.getPatronymic());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());

        userRepository.save(user);
    }

    public void login(
            final String username, final String password, final HttpServletRequest request
    ) throws CredentialNotFoundException, UsernameNotFoundException {

        final User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь не найден")
        );

        if (passwordEncoder.matches(password, user.getPassword())) {

            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username, password
            );
            final Authentication auth = authenticationManager.authenticate(token);

            final SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);

            final HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
        } else {
            throw new CredentialNotFoundException("Неверный пароль");
        }
    }

    @Transactional
    public void register(final RegisterDto dto) {

        final User user = User
                .builder()
                .lastname(dto.getLastname())
                .name(dto.getName())
                .patronymic(dto.getPatronymic())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .age(dto.getAge())
                .active(true)
                .build();

        userRepository.save(user);
    }
}
