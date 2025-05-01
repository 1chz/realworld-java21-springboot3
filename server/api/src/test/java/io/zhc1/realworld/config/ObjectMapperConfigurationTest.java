package io.zhc1.realworld.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("Object Mapper Configuration - DateTime Serialization and Deserialization")
class ObjectMapperConfigurationTest {
    ObjectMapper sut;

    @BeforeEach
    void setUp() {
        sut = new ObjectMapper().registerModule(new ObjectMapperConfiguration().iso8601SerializeModule());
    }

    @Test
    @DisplayName("When checking serialization capability, then should be able to serialize LocalDateTime")
    void whenCheckingSerializationCapability_thenShouldBeAbleToSerializeLocalDateTime() {
        assertTrue(sut.canSerialize(LocalDateTime.class));
    }

    @Test
    @DisplayName("When converting ISO8601 string to LocalDateTime, then should convert correctly")
    void whenConvertingISO8601StringToLocalDateTime_thenShouldConvertCorrectly() {
        // given
        String iso8601String = "2021-08-01T00:00:00.000Z";

        // when
        LocalDateTime localDateTime = sut.convertValue(iso8601String, LocalDateTime.class);

        // then
        assertThat(localDateTime).isEqualTo(LocalDateTime.of(2021, 8, 1, 0, 0, 0, 0));
    }

    @Test
    @DisplayName("When converting LocalDateTime to ISO8601 string, then should convert correctly")
    void whenConvertingLocalDateTimeToISO8601String_thenShouldConvertCorrectly() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 8, 1, 0, 0, 0, 0);

        // when
        String iso8601String = sut.convertValue(localDateTime, String.class);

        // then
        assertThat(iso8601String).isEqualTo("2021-08-01T00:00:00.000Z");
    }
}
