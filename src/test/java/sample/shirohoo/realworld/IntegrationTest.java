package sample.shirohoo.realworld;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Inherited
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {}
