package com.fourtwod.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.web.dto.UserDto;
import com.fourtwod.web.handler.ApiResult;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;

    private String token;

    @Before
    public void setup() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        String code = "front";
        String secretKey = "7e8b6e0ec18ca17a9fc54a20848656d0";

        ResultActions resultActions = mvc.perform(get("/token")
                        .param("code", code)
                        .param("secretKey", secretKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("OK")))
                .andExpect(jsonPath("$.data.user", is(code)))
                .andExpect(jsonPath("$.data.token", startsWith("Bearer ")));

        String test = resultActions.andReturn().getResponse().getContentAsString();

        ApiResult apiResult = new Gson().fromJson(test, ApiResult.class);
        Map<String, Object> map = (Map<String, Object>) apiResult.getData();
        token = (String) map.get("token");
    }

    @Test
    public void 사용자_로그인() throws Exception {
        String email = "test@test.com";
        String registrationId = "google";
        String name = "tester";

        UserDto userDto = UserDto.builder()
                .email(email)
                .registrationId(registrationId)
                .name(name)
                .build();

        String url = "http://localhost:" + port + "/user";

        //when
        mvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(new ObjectMapper().writeValueAsString(userDto)))
            .andExpect(status().isOk());

        //then
        User user = userRepository.findByUserId(UserId.builder()
                .email(email)
                .registrationId(registrationId)
                .build()).orElse(null);
        assertThat(user, is(notNullValue()));
        assertThat(user.getUserId().getEmail(), equalTo(email));
        assertThat(user.getUserId().getRegistrationId(), equalTo(registrationId));
        assertThat(user.getName(), equalTo(name));
    }
}
