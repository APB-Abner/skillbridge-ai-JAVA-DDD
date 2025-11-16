package br.com.fiap.skillbridge.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SkillbridgeAiApplicationTests {

    @Test
    void main_runsWithoutExceptions() {
        assertDoesNotThrow(() -> SkillbridgeApplication.main(new String[]{}));
    }

    @Test
	void contextLoads() {
	}

}
