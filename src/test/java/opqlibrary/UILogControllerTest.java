package opqlibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UILogControllerTest {
    private UILogController controller;

    @BeforeEach
    void setUp() {
        controller = new UILogController();
    }

    @Test
    void logUIAction_returnsOk() {
        String logEntry = "Clicked button";
        ResponseEntity<?> response = controller.logUIAction(logEntry);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
} 