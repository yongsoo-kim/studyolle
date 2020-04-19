package com.studyolle.studyolle.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.domain.Account;

@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {
	
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@AfterEach
	void afterEach(){
		accountRepository.deleteAll();
	}
	
	
	@WithAccount("yongsoo")
	@DisplayName("Profile update Form - Normal Input")
	@Test
	void showUpdateProfileForm() throws Exception{
		mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("profile"));		
	}
	
	
	
	@WithAccount("yongsoo")
	@DisplayName("Profile update - Normal Input")
	@Test
	void updateProfile() throws Exception{
		String bio = "Short self introduction";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
			.param("bio", bio)
			//Form with post, you need to have 'csrf' check always.
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
			.andExpect(flash().attributeExists("message"));
		
		Account yongsoo = accountRepository.findByNickname("yongsoo");
		assertEquals(bio, yongsoo.getBio());		
	}
	
	
	@WithAccount("yongsoo")
	@DisplayName("Profile update - Error Input")
	@Test
	void updateProfile_error() throws Exception{
		String bio = "Seeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
			.param("bio", bio)
			//Form with post, you need to have 'csrf' check always.
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("profile"))
			.andExpect(model().hasErrors());
		
		Account yongsoo = accountRepository.findByNickname("yongsoo");
		assertNull(yongsoo.getBio());
	}
	
	@WithAccount("yongsoo")
	@DisplayName("Password update form")
	@Test
	void updatePassword_form() throws Exception {
		mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("passwordForm"));
	}
	
	
	@WithAccount("yongsoo")
	@DisplayName("Password update - Normal Input")
	@Test
	void updatePassword_success() throws Exception {
		mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
				.param("newPassword", "1234567890")
				.param("newPasswordConfirm", "1234567890")
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
			.andExpect(flash().attributeExists("message"));
		
		Account yongsoo = accountRepository.findByNickname("yongsoo");
		assertTrue(passwordEncoder.matches("1234567890", yongsoo.getPassword()));
	}
	
	
	@WithAccount("yongsoo")
	@DisplayName("Password update - Wrong Input")
	@Test
	void updatePassword_fail() throws Exception {
		mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
				.param("newPassword", "12345678")
				.param("newPasswordConfirm", "111111111")
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("passwordForm"));
		// original password is "12345678"
		Account yongsoo = accountRepository.findByNickname("yongsoo");
		assertTrue(passwordEncoder.matches("12345678", yongsoo.getPassword()));
	}
	
	
}
