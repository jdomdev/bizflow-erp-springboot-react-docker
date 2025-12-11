package io.sunbit.app.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.sunbit.app.util.DateUtil;

public class DateUtilTest {

    @Test
    @DisplayName("Test formattingDate returns correct LocalDateTime")
    public void testFormattingDate() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 11, 10, 30, 0);
        LocalDateTime formatted = DateUtil.formattingDate(date);
        assertThat(formatted).isEqualTo(date);
    }
}
