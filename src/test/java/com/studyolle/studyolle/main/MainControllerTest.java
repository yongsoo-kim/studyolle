package com.studyolle.studyolle.main;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.SignUpForm;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname("yongsoo");
		signUpForm.setEmail("smilesigma@gmail.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
	}
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}

	@DisplayName("Login OK with email.")
	@Test
	void login_with_email() throws Exception {
		
		mockMvc.perform(post("/login")
				.param("username", "yongsoo")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("yongsoo"));
		
	}
	
	@DisplayName("Login OK with nickname.")
	@Test
	void login_with_nickname() throws Exception {
		
		mockMvc.perform(post("/login")
				.param("username", "smilesigma@gmail.com")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("yongsoo"));
		
	}
	
	
	@DisplayName("Login NG with nickname.")
	@Test
	void login_fail() throws Exception {
		
		mockMvc.perform(post("/login")
				.param("username", "111111111111")
				.param("password", "000000000000")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
		
	}
	
	
	@DisplayName("Login out.")
	@Test
	void logout() throws Exception {
		
		mockMvc.perform(post("/logout")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(unauthenticated());
		
	}
}
