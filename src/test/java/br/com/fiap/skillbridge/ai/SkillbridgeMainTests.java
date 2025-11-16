// SkillbridgeMainTests.java
package br.com.fiap.skillbridge.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SkillbridgeMainTests {


    @Test
    void main_runsWithoutExceptions() {
        assertDoesNotThrow(() -> SkillbridgeApplication.main(new String[]{}));
    }
}