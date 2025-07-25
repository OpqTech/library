package opqlibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {
        @Test
        @DisplayName("returns UserDetails when user exists")
        void returnsUserDetailsWhenUserExists() {
            User user = new User("alice", "encodedpass", "ROLE_USER");
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            UserDetails details = userService.loadUserByUsername("alice");
            assertThat(details.getUsername()).isEqualTo("alice");
            assertThat(details.getPassword()).isEqualTo("encodedpass");
            assertThat(details.getAuthorities()).extracting("authority").contains("ROLE_USER");
        }

        @Test
        @DisplayName("throws UsernameNotFoundException when user does not exist")
        void throwsWhenUserNotFound() {
            when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.loadUserByUsername("bob"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: bob");
        }

        @Test
        @DisplayName("logs and rethrows unexpected exceptions")
        void logsAndRethrowsUnexpectedExceptions() {
            when(userRepository.findByUsername("eve")).thenThrow(new RuntimeException("DB error"));
            assertThatThrownBy(() -> userService.loadUserByUsername("eve"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
        }
    }

    @Nested
    @DisplayName("saveUser")
    class SaveUser {
        @Test
        @DisplayName("encodes password and saves user")
        void encodesPasswordAndSavesUser() {
            when(passwordEncoder.encode("plainpass")).thenReturn("encodedpass");
            User user = new User("carol", "encodedpass", "ROLE_ADMIN");
            when(userRepository.save(any(User.class))).thenReturn(user);
            User saved = userService.saveUser("carol", "plainpass", "ROLE_ADMIN");
            assertThat(saved.getUsername()).isEqualTo("carol");
            assertThat(saved.getPassword()).isEqualTo("encodedpass");
            assertThat(saved.getRole()).isEqualTo("ROLE_ADMIN");
            verify(passwordEncoder).encode("plainpass");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("logs and rethrows exceptions from repository")
        void logsAndRethrowsExceptions() {
            when(passwordEncoder.encode("failpass")).thenReturn("encodedfail");
            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB save error"));
            assertThatThrownBy(() -> userService.saveUser("dave", "failpass", "ROLE_USER"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB save error");
        }
    }
} 