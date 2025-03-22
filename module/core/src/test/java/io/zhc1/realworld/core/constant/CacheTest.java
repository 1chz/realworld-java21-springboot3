package io.zhc1.realworld.core.constant;

import java.lang.reflect.Constructor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CacheTest {
    @Test
    @SuppressWarnings("ClassGetClass")
    void shouldNotInstantiation() {
        Assertions.assertThatThrownBy(() -> {
            Class<? extends Class> clazz = Cache.class.getClass();
            Constructor<? extends Class> privateConstructor = clazz.getDeclaredConstructor();
            privateConstructor.setAccessible(true);
            privateConstructor.newInstance();
        });
    }
}
