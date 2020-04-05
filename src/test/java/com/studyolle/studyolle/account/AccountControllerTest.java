package com.studyolle.studyolle.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import com.studyolle.studyolle.domain.Account;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	JavaMailSender javaMailSender;

	@DisplayName("Check 'sign-up' page can be displayed or not.")
	@Test
	void signUpForm() throws Exception {

		mockMvc.perform(get("/sign-up"))
			//.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("account/sign-up"))
			.andExpect(model().attributeExists("signUpForm"));

	}
	
	
	@DisplayName("'sign-up' - input value error")
	@Test
	void signUpSubmit_with_wrong_input() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "yongsoo")
				.param("email", "email")
				.param("password", "12345")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"));
		
	}
	
	
	
	@DisplayName("'sign-up' - input value correct")
	@Test
	void signUpSubmit_with_correct_input() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "yongsoo")
				.param("email", "smilesigma@gmail.com")
				.param("password", "123456789")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));
		
		Account account = accountRepository.findByEmail("smilesigma@gmail.com");
		assertNotNull(account);
		assertNotEquals(account.getPassword(), "123456789");
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}
}
