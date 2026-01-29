package org.example.anpfacturationbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(org.example.anpfacturationbackend.config.TestSecurityConfig.class)
class AnpFacturationBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
