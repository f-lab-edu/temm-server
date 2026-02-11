package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoogleUserProviderLoadTest {

    @Autowired
    GoogleUserProvider googleUserProvider;

    @Autowired
    GoogleIdTokenVerifier verifier;

    @Test
    void contextLoads() {
        assertThat(googleUserProvider).isNotNull();
        assertThat(verifier).isNotNull();
    }

}
