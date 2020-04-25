package com.studyolle.studyolle.settings;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.domain.Zone;
import com.studyolle.studyolle.settings.form.TagForm;
import com.studyolle.studyolle.settings.form.ZoneForm;
import com.studyolle.studyolle.tag.TagRepository;
import com.studyolle.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.domain.Account;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	ZoneRepository zoneRepository;

	private Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

	@BeforeEach
	void beforeEach(){
		zoneRepository.save(testZone);
	}

	@AfterEach
	void afterEach(){
		accountRepository.deleteAll();
		zoneRepository.deleteAll();
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


	@WithAccount("yongsoo")
	@DisplayName("Tags update form")
	@Test
	void update_tagsForm() throws Exception {
		mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
				.andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("whitelist"))
				.andExpect(model().attributeExists("tags"));
	}


	@WithAccount("yongsoo")
	@DisplayName("Adding Tags to an account")
	@Test
	void addTag() throws Exception {
		TagForm tagForm = new TagForm();
		tagForm.setTagTitle("newTag");

		mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
				.contentType(MediaType.APPLICATION_JSON)
				//.content("{\"tagTitle\": \"newTag\"}") -> you can do it like this too, but it will be painful. You can use objectMapper instead.
				.content(objectMapper.writeValueAsString(tagForm))
				.with(csrf()))
				.andExpect(status().isOk());

		//check tag is saved or not.
		Tag newTag = tagRepository.findByTitle("newTag");
		assertNotNull(newTag);

		Account account = accountRepository.findByNickname("yongsoo");
		//To get 'persist' status, add @Transactional annotation.
		assertTrue(account.getTags().contains(newTag));
	}


	@WithAccount("yongsoo")
	@DisplayName("removing Tags to an account")
	@Test
	void removeTag() throws Exception {
		Account account = accountRepository.findByNickname("yongsoo");
		Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
		accountService.addTag(account, newTag);

		assertTrue(account.getTags().contains(newTag));


		TagForm tagForm = new TagForm();
		tagForm.setTagTitle("newTag");

		mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagForm))
				.with(csrf()))
				.andExpect(status().isOk());

		Tag oldTag = tagRepository.findByTitle("newTag");

		assertFalse(account.getTags().contains(oldTag));
	}

	@WithAccount("yongsoo")
	@DisplayName("Zones update form")
	@Test
	void update_zonesForm() throws Exception {
		mockMvc.perform(get(SettingsController.SETTINGS_ZONES_URL))
				.andExpect(view().name(SettingsController.SETTINGS_ZONES_VIEW_NAME))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("whitelist"))
				.andExpect(model().attributeExists("zones"));
	}


	@WithAccount("yongsoo")
	@DisplayName("Adding Zones to an account")
	@Test
	void addZone() throws Exception {
		ZoneForm zoneForm = new ZoneForm();
		zoneForm.setZoneName(testZone.toString());

		mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(zoneForm))
				.with(csrf()))
				.andExpect(status().isOk());

		//check zone info is saved or not.
		Zone newZone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
		assertNotNull(newZone);

		Account account = accountRepository.findByNickname("yongsoo");
		//To get 'persist' status, add @Transactional annotation.
		assertTrue(account.getZones().contains(newZone));
	}


	@WithAccount("yongsoo")
	@DisplayName("removing Zones to an account")
	@Test
	void removeZone() throws Exception {
		Account account = accountRepository.findByNickname("yongsoo");
		Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
		accountService.addZone(account, zone);

		assertTrue(account.getZones().contains(zone));

		ZoneForm zoneForm = new ZoneForm();
		zoneForm.setZoneName(testZone.toString());

		mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/remove")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(zoneForm))
				.with(csrf()))
				.andExpect(status().isOk());

		assertFalse(account.getZones().contains(testZone));
	}
}
