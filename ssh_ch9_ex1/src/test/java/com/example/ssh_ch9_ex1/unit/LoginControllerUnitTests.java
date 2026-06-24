package com.example.ssh_ch9_ex1.unit;

import com.example.ssh_ch9_ex1.controllers.LoginController;
import com.example.ssh_ch9_ex1.processors.LoginProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoginControllerUnitTests {

    @Mock
    private Model model;

    @Mock
    private LoginProcessor loginProcessor;

    @InjectMocks
    private LoginController loginController;

    @Test
    @DisplayName("""
        GIVEN  Valid credentials are provided
        WHEN   The loginPost method is called
        THEN   The user is redirected to the login page with a success message
        """)
    public void loginPostTestSuccess() {
        given(loginProcessor.login())
                .willReturn(true);

        String result = loginController.loginPost("natalie", "password", model);

        assertEquals("login.html", result);
        verify(model).addAttribute("message", "You are now logged in.");
    }

    @Test
    @DisplayName("""
        GIVEN Invalid credentials are provided
        WHEN  The loginPost method is called
        THEN  The user is redirected to the login page with a failure message
        """)
    public void loginPostTestFailure() {
        given(loginProcessor.login())
                .willReturn(false);

        String result = loginController.loginPost("natalie", "wrong", model);

        assertEquals("login.html", result);
        verify(model).addAttribute("message", "Login failed!");
    }
}
