package sample.shirohoo.realworld.config;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ObjectMapperConfigurationTest {
    ObjectMapper sut;

    @BeforeEach
    void setUp() {
        ObjectMapperConfiguration configuration = new ObjectMapperConfiguration();
        sut = new ObjectMapper().registerModule(configuration.iso8601SerializeModule());
    }

    @Test
    void canSerialize_LocalDateTime() {
        assertTrue(sut.canSerialize(LocalDateTime.class));
    }

    @Test
    void convert_ISO8601String_toLocalDateTime() {
        // given
        String iso8601String = "2021-08-01T00:00:00.000Z";

        // when
        LocalDateTime localDateTime = sut.convertValue(iso8601String, LocalDateTime.class);

        // then
        assertThat(localDateTime).isEqualTo(LocalDateTime.of(2021, 8, 1, 0, 0, 0, 0));
    }

    @Test
    void convert_LocalDateTime_toISO8601String() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 8, 1, 0, 0, 0, 0);

        // when
        String iso8601String = sut.convertValue(localDateTime, String.class);

        // then
        assertThat(iso8601String).isEqualTo("2021-08-01T00:00:00.000Z");
    }
}
