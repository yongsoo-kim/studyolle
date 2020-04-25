package com.studyolle.studyolle.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.domain.Zone;
import com.studyolle.studyolle.settings.form.*;
import com.studyolle.studyolle.settings.validator.Nicknamevalidator;
import com.studyolle.studyolle.settings.validator.PasswordFormValidator;
import com.studyolle.studyolle.tag.TagRepository;
import com.studyolle.studyolle.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.studyolle.studyolle.settings.SettingsController.ROOT;
import static com.studyolle.studyolle.settings.SettingsController.SETTINGS;

@Controller
@RequestMapping(ROOT + SETTINGS)
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
	
	
	@GetMapping(PROFILE)
	public String updateProfileForm(@CurrentUser Account account, Model model) {
		
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, Profile.class));	
		return SETTINGS + PROFILE;

	}
	
	
	@PostMapping(PROFILE)
	public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
		if(errors.hasErrors()) {
			model.addAttribute("account", account);
			return SETTINGS + PROFILE;
		}
		
		accountService.updateProfile(account, profile);
		attributes.addFlashAttribute("message","Profile has been updated.");
		
		//GET-POST-REDIRECT pattern
		//By doing this, we can prevent users from submitting form again.
		return "redirect:/" + SETTINGS + PROFILE;
	}
	
	
	@GetMapping(PASSWORD)
	public String updatePasswordForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		// this is same as 
		// model.addAttribute("passwordForm", new PasswordForm());
		model.addAttribute(new PasswordForm());
		return SETTINGS + PASSWORD;

	}
	
	
	@PostMapping(PASSWORD)
	public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes) {
		
		
		if(errors.hasErrors()) {
			model.addAttribute("account", account);
			return SETTINGS + PASSWORD;
		}
		
		accountService.updatePassword(account, passwordForm.getNewPassword());
		attributes.addFlashAttribute("message", "패스워드를 변경했습니다");
		return "redirect:/"  + SETTINGS + PASSWORD;
	}
	
	
	@GetMapping(NOTIFICATIONS)
	public String updateNotificationForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, Notifications.class));
		
		return SETTINGS + NOTIFICATIONS;
	}
	
	
	@PostMapping(NOTIFICATIONS)
	public String updateNotification(@CurrentUser Account account, @Valid Notifications notifications, Errors errors, Model model, RedirectAttributes attributes) {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS + NOTIFICATIONS;

		}
		
		accountService.updateNotifications(account, notifications);
		attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");

		return "redirect:/" + SETTINGS +NOTIFICATIONS;
	}
	
	
	@GetMapping(ACCOUNT)
	public String updateAccountForm(@CurrentUser Account account, Model model) {
		model.addAttribute("account", account);
		model.addAttribute(modelMapper.map(account, NicknameForm.class));
		return SETTINGS + ACCOUNT;

	}
	
	
	@PostMapping(ACCOUNT)
	public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,  Model model, RedirectAttributes attributes) {
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS + ACCOUNT;
		}
		
		accountService.updateNickName(account, nicknameForm.getNickname());
		attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
		
		return "redirect:/" + SETTINGS + ACCOUNT;
	}


	@GetMapping(TAGS)
	public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
		model.addAttribute("account", account);

		Set<Tag> tags = accountService.getTags(account);
		model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

		List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

		//to parse in Json, use ObjectMapper
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

		return SETTINGS + TAGS;

	}

	@PostMapping(TAGS + "/add")
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


	@PostMapping(TAGS + "/remove")
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

	@GetMapping(ZONES)
	public String updateZones(@CurrentUser Account account, Model model) throws JsonProcessingException {

		Set<Zone> zones = accountService.getZones(account);

		model.addAttribute("account", account);
		model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

		List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		//to parse in Json, use ObjectMapper
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

		return SETTINGS + ZONES;
	}


	@PostMapping(ZONES + "/add")
	@ResponseBody
	public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

		if (zone == null) {
			return ResponseEntity.badRequest().build();
		}

		accountService.addZone(account, zone);
		return ResponseEntity.ok().build();
	}

	@PostMapping(ZONES + "/remove")
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
