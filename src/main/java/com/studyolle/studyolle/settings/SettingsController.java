package com.studyolle.studyolle.settings;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.domain.Zone;
import com.studyolle.studyolle.settings.form.*;
import com.studyolle.studyolle.tag.TagRepository;
import com.studyolle.studyolle.zone.ZoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.settings.validator.Nicknamevalidator;
import com.studyolle.studyolle.settings.validator.PasswordFormValidator;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.studyolle.studyolle.settings.SettingsController.ROOT;
import static com.studyolle.studyolle.settings.SettingsController.SETTINGS;

@Controller
//@RequestMapping(ROOT + SETTINGS)
@RequiredArgsConstructor
public class SettingsController {

	static final String ROOT = "/";
	static final String SETTINGS = "settings";
	static final String PROFILE = "/profile";
	static final String PASSWORD = "/password";
	static final String NOTIFICATIONS = "/notifications";
	static final String ACCOUNT = "/account";
	static final String TAGS = "/tags";
	static final String ZONES = "/zones";
	
	static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
	static final String SETTINGS_PROFILE_URL="/settings/profile";
	
	static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
	static final String SETTINGS_PASSWORD_URL="/settings/password";
	
	static final String SETTINGS_NOTIFICATION_VIEW_NAME = "/settings/notifications";
	static final String SETTINGS_NOTIFICATION_URL="/settings/notifications";

	static final String SETTINGS_ACCOUNT_VIEW_NAME = "/settings/account";
	static final String SETTINGS_ACCOUNT_URL="/settings/account";

	static final String SETTINGS_TAGS_VIEW_NAME = "/settings/tags";
	static final String SETTINGS_TAGS_URL="/settings/tags";

	static final String SETTINGS_ZONES_VIEW_NAME = "/settings/zones";
	static final String SETTINGS_ZONES_URL="/settings/zones";

	private final AccountService accountService;
	private final ModelMapper modelMapper;
	private final Nicknamevalidator nicknameValidator;
	private final TagRepository tagRepository;
	private final ObjectMapper objectMapper;
	private final ZoneRepository zoneRepository;
	
	@InitBinder("passwordForm")
	public void passwordForminitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidator());
	}
	
	@InitBinder("nicknameForm")
	public void nicknameForminitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(nicknameValidator);
	}
	
	
	@GetMapping(SETTINGS_PROFILE_URL)
	public String updateProfileForm(@CurrentUser Account account, Model model) {
		
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, Profile.class));	
		return SETTINGS_PROFILE_VIEW_NAME;
	}
	
	
	@PostMapping(SETTINGS_PROFILE_URL)
	public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
		if(errors.hasErrors()) {
			model.addAttribute("account", account);
			return SETTINGS_PROFILE_VIEW_NAME;
		}
		
		accountService.updateProfile(account, profile);
		attributes.addFlashAttribute("message","Profile has been updated.");
		
		//GET-POST-REDIRECT pattern
		//By doing this, we can prevent users from submitting form again.
		return "redirect:" + SETTINGS_PROFILE_VIEW_NAME;
	}
	
	
	@GetMapping(SETTINGS_PASSWORD_URL)
	public String updatePasswordForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		// this is same as 
		// model.addAttribute("passwordForm", new PasswordForm());
		model.addAttribute(new PasswordForm());
		return SETTINGS_PASSWORD_VIEW_NAME;
	}
	
	
	@PostMapping(SETTINGS_PASSWORD_URL)
	public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes) {
		
		
		if(errors.hasErrors()) {
			model.addAttribute("account", account);
			return SETTINGS_PASSWORD_VIEW_NAME;
		}
		
		accountService.updatePassword(account, passwordForm.getNewPassword());
		attributes.addFlashAttribute("message", "패스워드를 변경했습니다");
		return "redirect:" +  SETTINGS_PASSWORD_URL;
	}
	
	
	@GetMapping(SETTINGS_NOTIFICATION_URL)
	public String updateNotificationForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, Notifications.class));
		
		return SETTINGS_NOTIFICATION_VIEW_NAME;
	}
	
	
	@PostMapping(SETTINGS_NOTIFICATION_URL)
	public String updateNotification(@CurrentUser Account account, @Valid Notifications notifications, Errors errors, Model model, RedirectAttributes attributes) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_NOTIFICATION_VIEW_NAME;
		}
		
		accountService.updateNotifications(account, notifications);
		attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");

		return "redirect:" + SETTINGS_NOTIFICATION_URL;
	}
	
	
	@GetMapping(SETTINGS_ACCOUNT_URL)
	public String updateAccountForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, NicknameForm.class));
		return SETTINGS_ACCOUNT_VIEW_NAME;
	}
	
	
	@PostMapping(SETTINGS_ACCOUNT_URL)
	public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,  Model model, RedirectAttributes attributes) {
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_ACCOUNT_VIEW_NAME;
		}
		
		accountService.updateNickName(account, nicknameForm.getNickname());
		attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
		
		return "redirect:" + SETTINGS_ACCOUNT_URL;
	}


	@GetMapping(SETTINGS_TAGS_URL)
	public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
		model.addAttribute("account", account);

		Set<Tag> tags = accountService.getTags(account);
		model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

		List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

		//to parse in Json, use ObjectMapper
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

		return SETTINGS_TAGS_VIEW_NAME;
	}

	@PostMapping(SETTINGS_TAGS_URL + "/add")
	@ResponseBody
	public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		String title = tagForm.getTagTitle();

		//Same code, but using Optional
//		Tag tag = tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder()
//				.title(tagForm.getTagTitle())
//				.build()));
        //  Tag existence check.
		Tag tag = tagRepository.findByTitle(title);
		if (tag == null) {
			tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
		}

		accountService.addTag(account, tag);
		return ResponseEntity.ok().build();
	}


	@PostMapping(SETTINGS_TAGS_URL + "/remove")
	@ResponseBody
	public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		String title = tagForm.getTagTitle();

		Tag tag = tagRepository.findByTitle(title);
		if (tag == null) {
			return ResponseEntity.badRequest().build();
		}
		accountService.removeTag(account, tag);
		return ResponseEntity.ok().build();
	}

	@GetMapping(SETTINGS_ZONES_URL)
	public String updateZones(@CurrentUser Account account, Model model) throws JsonProcessingException {

		Set<Zone> zones = accountService.getZones(account);

		model.addAttribute("account", account);
		model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

		List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		//to parse in Json, use ObjectMapper
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

		return SETTINGS_ZONES_VIEW_NAME;
	}


	@PostMapping(SETTINGS_ZONES_URL + "/add")
	@ResponseBody
	public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

		if (zone == null) {
			return ResponseEntity.badRequest().build();
		}

		accountService.addZone(account, zone);
		return ResponseEntity.ok().build();
	}

	@PostMapping(SETTINGS_ZONES_URL + "/remove")
	@ResponseBody
	public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

		if (zone == null) {
			return ResponseEntity.badRequest().build();
		}

		accountService.removeZone(account, zone);
		return ResponseEntity.ok().build();
	}

}
