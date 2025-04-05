package io.zhc1.realworld.config;

import java.lang.reflect.Constructor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CacheNameTest {
    @Test
    @SuppressWarnings("ClassGetClass")
    void shouldNotInstantiation() {
        Assertions.assertThatThrownBy(() -> {
            Class<? extends Class> clazz = CacheName.class.getClass();
            Constructor<? extends Class> privateConstructor = clazz.getDeclaredConstructor();
            privateConstructor.setAccessible(true);
            privateConstructor.newInstance();
        });
    }
}
