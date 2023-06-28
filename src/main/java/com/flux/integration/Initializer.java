package com.flux.integration;

import com.flux.integration.config.SessionConfiguration;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class Initializer extends AbstractHttpSessionApplicationInitializer {

    public Initializer() {
        super(SessionConfiguration.class);
    }
}
