package opqlibrary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        logger.info("Attempting to register user: {}", request.getUsername());
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                logger.warn("Registration failed: username '{}' already exists", request.getUsername());
                return ResponseEntity.badRequest().body("Username already exists");
            }
            userService.saveUser(request.getUsername(), request.getPassword(), "ROLE_USER");
            logger.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok().body("User registered successfully");
        } catch (Exception e) {
            logger.error("Error registering user '{}': {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
} 