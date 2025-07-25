package opqlibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import jakarta.servlet.http.HttpServletRequest;

class PasswordResetControllerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Model model;
    @InjectMocks
    private PasswordResetController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("requestReset")
    class RequestReset {
        @Test
        @DisplayName("returns generic message if user does not exist")
        void returnsGenericMessageIfUserNotFound() {
            when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
            HttpServletRequest request = mock(HttpServletRequest.class);
            ResponseEntity<?> response = controller.requestReset("nouser", request);
            assertThat(response.getBody().toString()).contains("If the user exists");
        }

        @Test
        @DisplayName("creates and saves token if user exists")
        void createsAndSavesTokenIfUserExists() {
            User user = new User("alice", "pass", "ROLE_USER");
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(tokenRepository.findByUsername("alice")).thenReturn(Optional.empty());
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getRequestURI()).thenReturn("/api/password-reset/request");
            when(request.getScheme()).thenReturn("http");
            when(request.getServerName()).thenReturn("localhost");
            when(request.getServerPort()).thenReturn(8080);
            ResponseEntity<?> response = controller.requestReset("alice", request);
            assertThat(response.getBody().toString()).contains("http://localhost:8080/password-reset?token=");
            verify(tokenRepository).save(any(PasswordResetToken.class));
        }
    }

    @Nested
    @DisplayName("showResetForm")
    class ShowResetForm {
        @Test
        @DisplayName("shows error if token is invalid or expired")
        void showsErrorIfTokenInvalidOrExpired() {
            when(tokenRepository.findByToken("badtoken")).thenReturn(Optional.empty());
            String view = controller.showResetForm("badtoken", model);
            verify(model).addAttribute(eq("error"), anyString());
            assertThat(view).isEqualTo("password-reset");
        }

        @Test
        @DisplayName("shows form if token is valid")
        void showsFormIfTokenValid() {
            PasswordResetToken token = new PasswordResetToken("alice", "goodtoken", LocalDateTime.now().plusMinutes(10));
            when(tokenRepository.findByToken("goodtoken")).thenReturn(Optional.of(token));
            String view = controller.showResetForm("goodtoken", model);
            verify(model).addAttribute("token", "goodtoken");
            assertThat(view).isEqualTo("password-reset");
        }
    }

    @Nested
    @DisplayName("confirmReset")
    class ConfirmReset {
        @Test
        @DisplayName("returns error if token is invalid or expired")
        void returnsErrorIfTokenInvalidOrExpired() {
            when(tokenRepository.findByToken("badtoken")).thenReturn(Optional.empty());
            ResponseEntity<?> response = controller.confirmReset("badtoken", "newpass");
            assertThat(response.getStatusCodeValue()).isEqualTo(400);
            assertThat(response.getBody().toString()).contains("Invalid or expired token");
        }

        @Test
        @DisplayName("returns error if user not found")
        void returnsErrorIfUserNotFound() {
            PasswordResetToken token = new PasswordResetToken("nouser", "token", LocalDateTime.now().plusMinutes(10));
            when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
            when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
            ResponseEntity<?> response = controller.confirmReset("token", "newpass");
            assertThat(response.getStatusCodeValue()).isEqualTo(400);
            assertThat(response.getBody().toString()).contains("User not found");
        }

        @Test
        @DisplayName("resets password and deletes token if valid")
        void resetsPasswordAndDeletesTokenIfValid() {
            PasswordResetToken token = new PasswordResetToken("alice", "token", LocalDateTime.now().plusMinutes(10));
            User user = new User("alice", "oldpass", "ROLE_USER");
            when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
            ResponseEntity<?> response = controller.confirmReset("token", "newpass");
            assertThat(user.getPassword()).isEqualTo("encodedpass");
            verify(userRepository).save(user);
            verify(tokenRepository).delete(token);
            assertThat(response.getBody().toString()).contains("Password reset successful");
        }
    }
} 