package opqlibrary;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class PasswordResetTokenTest {
    @Test
    void constructorAndGettersWork() {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        PasswordResetToken token = new PasswordResetToken("alice", "sometoken", expiry);
        assertThat(token.getUsername()).isEqualTo("alice");
        assertThat(token.getToken()).isEqualTo("sometoken");
        assertThat(token.getExpiryDate()).isEqualTo(expiry);
    }

    @Test
    void settersWork() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUsername("bob");
        token.setToken("othertoken");
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);
        token.setExpiryDate(expiry);
        token.setId(42L);
        assertThat(token.getUsername()).isEqualTo("bob");
        assertThat(token.getToken()).isEqualTo("othertoken");
        assertThat(token.getExpiryDate()).isEqualTo(expiry);
        assertThat(token.getId()).isEqualTo(42L);
    }

    @Test
    void canCheckIfTokenIsExpired() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(1);
        PasswordResetToken token = new PasswordResetToken("alice", "expired", past);
        assertThat(token.getExpiryDate().isBefore(LocalDateTime.now())).isTrue();
    }
} 