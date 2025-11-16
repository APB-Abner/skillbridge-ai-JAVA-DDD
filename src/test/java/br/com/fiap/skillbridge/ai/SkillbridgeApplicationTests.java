// java
package br.com.fiap.skillbridge.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class SkillbridgeApplicationTests {

    @Test
    void contextLoads() {
        // verifies Spring context starts with isolated test DB
    }
}