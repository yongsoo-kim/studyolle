package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.infra.mail.EmailMessage;
import com.studyolle.studyolle.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class AccountControllerTest extends AbstractContainerBaseTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	EmailService emailService;

	@DisplayName("Check 'sign-up' page can be displayed or not.")
	@Test
	void signUpForm() throws Exception {

		mockMvc.perform(get("/sign-up"))
			//.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("account/sign-up"))
			.andExpect(model().attributeExists("signUpForm"))
			.andExpect(unauthenticated());

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
				.andExpect(view().name("account/sign-up"))
				.andExpect(unauthenticated());;
		
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
				.andExpect(view().name("redirect:/"))
				.andExpect(authenticated().withUsername("yongsoo"));
		
		Account account = accountRepository.findByEmail("smilesigma@gmail.com");
		assertNotNull(account);
		assertNotEquals(account.getPassword(), "123456789");
	    assertNotNull(account.getEmailCheckToken());
		then(emailService).should().sendEmail(any(EmailMessage.class));
	}
	
	
	
	@DisplayName("'check-email-token' - wrong token input")
	@Test
	void checkEmailToken_with_wrong_input() throws Exception {
		mockMvc.perform(get("/check-email-token")
				.param("token", "abcdefg")
				.param("email", "smilesigma@gmail.com"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("error"))
				.andExpect(view().name("account/checked-email"))
				.andExpect(unauthenticated());
	}
	
	
	@DisplayName("'check-email-token' - correct token input")
	@Test
	void checkEmailToken_with_correct_input() throws Exception {
		
		Account account = Account.builder()
				.email("smilesigma@gmail.com")
				.password("123456789")
				.nickname("yongsoo")
				.build();
		
		Account newAccount = accountRepository.save(account);
		newAccount.generateEmailCheckToken();
		
		
		mockMvc.perform(get("/check-email-token")
				.param("token", newAccount.getEmailCheckToken())
				.param("email", newAccount.getEmail()))
				.andExpect(status().isOk())
				.andExpect(model().attributeDoesNotExist("error"))
				.andExpect(model().attributeExists("nickname"))
				.andExpect(model().attributeExists("numberOfUser"))
				.andExpect(view().name("account/checked-email"))
				.andExpect(authenticated());
	}
}
