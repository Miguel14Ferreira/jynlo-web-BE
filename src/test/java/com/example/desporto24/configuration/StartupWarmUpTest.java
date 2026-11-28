package com.example.desporto24.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartupWarmUpTest {
    @Test
    void buildsControllersBeforeTheWebServerAndSkipsOneThatFails() {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeanNamesForAnnotation(RestController.class)).thenReturn(new String[]{"a", "broken", "b"});
        when(context.getBean("a")).thenReturn(new Object());
        when(context.getBean("broken")).thenThrow(new IllegalStateException("cannot build"));
        when(context.getBean("b")).thenReturn(new Object());
        when(context.getBean(DataSource.class)).thenThrow(new IllegalStateException("no database in this test"));

        StartupWarmUp warmUp = new StartupWarmUp(context);
        warmUp.start();

        verify(context).getBean("a");
        verify(context).getBean("b");
        assertTrue(warmUp.isRunning());
        assertTrue(warmUp.getPhase() < Integer.MAX_VALUE - 1, "must run before the web server starts");
    }
}
