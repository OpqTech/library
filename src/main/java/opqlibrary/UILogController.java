package opqlibrary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
public class UILogController {
    private static final Logger logger = LoggerFactory.getLogger(UILogController.class);

    @PostMapping
    public ResponseEntity<?> logUIAction(@RequestBody String logEntry) {
        // Log UI action to stdout (container log)
        logger.info("UI Action Log: {}", logEntry);
        return ResponseEntity.ok().build();
    }
} 