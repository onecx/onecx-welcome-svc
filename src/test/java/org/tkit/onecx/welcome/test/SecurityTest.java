package org.tkit.onecx.welcome.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/images/id", 404, List.of("ocx-wc:read"), "get");
        config.addConfig("write", "/internal/images/info", 400, List.of("ocx-wc:write"), "post");
        return config;
    }
}
