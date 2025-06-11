package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.andres.usersapp.usersbackend.dto.user.UserWithRolesAndPermissionsDto;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Role;
import com.springboot.backend.andres.usersapp.usersbackend.entities.User;
import com.springboot.backend.andres.usersapp.usersbackend.models.IUser;
import com.springboot.backend.andres.usersapp.usersbackend.models.UserRequest;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.RoleRepository;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository repository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;
    
    
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public User save(User user) {
        user.setRoles(getRoles(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

      @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setEmail(user.getEmail());
            userDb.setLastname(user.getLastname());
            userDb.setName(user.getName());
            userDb.setUsername(user.getUsername());

            userDb.setRoles(getRoles(user));
            return Optional.of(repository.save(userDb));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        optionalRoleUser.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }
        return roles;
    }

    @Override
    public void assignRolePermissions(Long userId, List<Long> rolePermissionIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assignRolePermissions'");
    }

    @Override
    public List<UserWithRolesAndPermissionsDto> getUsersWithRolesAndPermissions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsersWithRolesAndPermissions'");
    }

@Override
public void assignRoleToUser(Long userId, Long roleId) {
    User user = repository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

    Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + roleId));

    if (!user.getRoles().contains(role)) {
        user.getRoles().add(role);
    }

    repository.save(user);
}

@Override
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIdsToUpdate) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        // ¡LA LÍNEA MODIFICADA ESTÁ AQUÍ!
        List<Role> newRoles = StreamSupport.stream(roleRepository.findAllById(roleIdsToUpdate).spliterator(), false)
                                           .collect(Collectors.toList());

        if (newRoles.size() != roleIdsToUpdate.size()) {
            Set<Long> foundRoleIds = newRoles.stream().map(Role::getId).collect(Collectors.toSet());
            String missingIds = roleIdsToUpdate.stream()
                                    .filter(id -> !foundRoleIds.contains(id))
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Uno o más roles no encontrados para los IDs: " + missingIds);
        }

        user.setRoles(newRoles);
        repository.save(user);
    }
 
}

    



