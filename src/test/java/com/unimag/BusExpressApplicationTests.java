package com.unimag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(AbstractRepositoryTest.class)
@SpringBootTest
class BusExpressApplicationTests {

    @Test
    void contextLoads() {
    }

}
