package dev.charles.SimpleService.oauth;

import dev.charles.SimpleService.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuthTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("Given we don't have any bearer token ")
    class WithoutBearerTokenTest{
        @Nested
        @DisplayName("When accessing a server using methods")
        class AccessWithoutGET{
            @Test
            @DisplayName("Then you will receive a 401 status when using the get method")
            void GetMethod() throws Exception {
                mockMvc.perform(get("/"))
                        .andExpect(status().isUnauthorized());
            }
            @Test
            @DisplayName("Then you will receive a 401 status when using the put method")
            void PutMethod() throws Exception {
                mockMvc.perform(put("/"))
                        .andExpect(status().isUnauthorized());
            }
            @Test
            @DisplayName("Then you will receive a 401 status when using the post method")
            void PostMethod() throws Exception {
                mockMvc.perform(post("/"))
                        .andExpect(status().isUnauthorized());
            }
            @Test
            @DisplayName("Then you will receive a 401 status when using the delete method")
            void DeleteMethod() throws Exception {
                mockMvc.perform(delete("/"))
                        .andExpect(status().isUnauthorized());
            }
            @Test
            @DisplayName("Then you will receive a 401 status when using the patch method")
            void PatchMethod() throws Exception {
                mockMvc.perform(patch("/"))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("Given we have a bearer token")
    class WithBearerTokenTest{
        String wrongBearerToken = "asdjsadeee";

        @Nested
        @DisplayName("When access this server with a invalid token ")
        class AccessWithInvalidToken{
            @Test
            @DisplayName("Then this server throws AuthenticationServiceException ")
            void WrongBearerToken() throws Exception {
                Throwable throwable = Assertions.catchThrowable(()->mockMvc.perform(put("/")
                                .header("Authorization", String.format("Bearer %s", wrongBearerToken))
                        )
                );
                assertThat(throwable).isInstanceOf(AuthenticationServiceException.class);
            }

        }

    }



}
