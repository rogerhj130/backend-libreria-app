package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.andres.usersapp.usersbackend.entities.User;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

   @Transactional(readOnly = true)
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = repository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            String.format("Username %s no existe en el sistema", username)));

   List<GrantedAuthority> authorities = user.getRoles().stream()
    .flatMap(role -> role.getRolePermissions().stream()
        .map(rolePermiso -> new SimpleGrantedAuthority(rolePermiso.getPermission().getName()))
    )
    .collect(Collectors.toList());
    return new org.springframework.security.core.userdetails.User(
        username,
        user.getPassword(),
        true, true, true, true,
        authorities
    );
}

}
