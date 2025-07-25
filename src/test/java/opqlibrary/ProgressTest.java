package opqlibrary;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProgressTest {
    @Test
    void constructorAndGettersWork() {
        Progress progress = new Progress(2, 3, 1);
        assertThat(progress.completed()).isEqualTo(2);
        assertThat(progress.toRead()).isEqualTo(3);
        assertThat(progress.inProgress()).isEqualTo(1);
    }

    @Test
    void notStartedReturnsZeroes() {
        Progress progress = Progress.notStarted();
        assertThat(progress.completed()).isZero();
        assertThat(progress.toRead()).isZero();
        assertThat(progress.inProgress()).isZero();
    }
} 