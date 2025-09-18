package io.zhc1.realworld;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("RealWorld Application - Spring Context Initialization")
public class RealWorldApplicationTest {
    @Test
    @DisplayName("When application starts, then Spring context should load successfully")
    void contextLoads() {
        assertTrue(true);
    }
}
