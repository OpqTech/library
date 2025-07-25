package opqlibrary;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public DataInitializer(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userService.saveUser("admin", "admin123", "ROLE_ADMIN");
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            userService.saveUser("user", "user123", "ROLE_USER");
        }
    }
} 