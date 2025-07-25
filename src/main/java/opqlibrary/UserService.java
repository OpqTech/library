package opqlibrary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            opqlibrary.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            UserBuilder builder = User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().replace("ROLE_", ""));
            return builder.build();
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }

    public opqlibrary.User saveUser(String username, String password, String role) {
        try {
            opqlibrary.User user = new opqlibrary.User(username, passwordEncoder.encode(password), role);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saving user: {}", username, e);
            throw e;
        }
    }
} 