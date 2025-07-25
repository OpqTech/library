package opqlibrary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Controller
public class PasswordResetController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @PostMapping("/api/password-reset/request")
    @ResponseBody
    public ResponseEntity<?> requestReset(@RequestParam String username, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            // Always return OK to avoid user enumeration
            return ResponseEntity.ok().body("If the user exists, a reset link will be provided.");
        }
        String token = generateToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        tokenRepository.findByUsername(username).ifPresent(tokenRepository::delete);
        tokenRepository.save(new PasswordResetToken(username, token, expiry));
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        String resetLink = baseUrl + "/password-reset?token=" + token;
        // In production, email the link. For demo, return it in the response.
        return ResponseEntity.ok().body("Reset link: " + resetLink);
    }

    @GetMapping("/password-reset")
    public String showResetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "password-reset";
        }
        model.addAttribute("token", token);
        return "password-reset";
    }

    @PostMapping("/api/password-reset/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmReset(@RequestParam String token, @RequestParam String password) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
        PasswordResetToken prt = tokenOpt.get();
        Optional<User> userOpt = userRepository.findByUsername(prt.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        tokenRepository.delete(prt);
        return ResponseEntity.ok().body("Password reset successful. You can now log in.");
    }
} 