package com.fourtwod.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TokenController.class)
@AutoConfigureMockMvc
public class TokenControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void token_발급() throws Exception {
        String code = "front";
        String secretKey = "7e8b6e0ec18ca17a9fc54a20848656d0";

        mvc.perform(get("/token")
                .param("code", code)
                .param("secretKey", secretKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.message", is("OK")))
            .andExpect(jsonPath("$.data.user", is(code)))
            .andExpect(jsonPath("$.data.token", startsWith("Bearer ")));
    }
}
