package sample.shirohoo.realworld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("RealWorld Application Test")
public class RealWorldApplicationTest {
    @Test
    @DisplayName("spring context of RealWorld Application is loaded.")
    void contextLoads() {}
}
