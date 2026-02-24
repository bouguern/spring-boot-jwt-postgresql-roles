package bouguern.tuto.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bouguern.tuto.demo.constants.Role;
import bouguern.tuto.demo.entities.UserEntity;
import bouguern.tuto.demo.exceptions.InvalidCredentialsException;
import bouguern.tuto.demo.exceptions.UserAlreadyExistsException;
import bouguern.tuto.demo.exceptions.UserNotFoundException;
import bouguern.tuto.demo.records.AuthResponse;
import bouguern.tuto.demo.records.LoginRequest;
import bouguern.tuto.demo.records.RegisterRequest;
import bouguern.tuto.demo.records.UserResponse;
import bouguern.tuto.demo.repositories.UserRepository;
import bouguern.tuto.demo.security.JwtUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        
        // Assign default role if none provided
        Set<Role> roles = request.roles() != null && !request.roles().isEmpty() 
            ? request.roles() 
            : Set.of(Role.USER);
        
        // Create user
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .enabled(true)
                .build();
        
        UserEntity savedUser = userRepository.save(user);
        
        // Generate token
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles()
        );
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
            
            // Get user details
            UserEntity user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            // Generate token
            String token = jwtUtil.generateToken(user);
            
            return new AuthResponse(
                    token,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles()
            );
            
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return mapToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUserRoles(Long userId, Set<Role> roles) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        user.setRoles(roles);
        UserEntity updatedUser = userRepository.save(user);
        
        return mapToUserResponse(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    private UserResponse mapToUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt().toString()
        );
    }
}