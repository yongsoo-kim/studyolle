package com.studyolle.studyolle.settings;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.settings.form.NicknameForm;
import com.studyolle.studyolle.settings.form.Notifications;
import com.studyolle.studyolle.settings.form.PasswordForm;
import com.studyolle.studyolle.settings.form.Profile;
import com.studyolle.studyolle.settings.validator.Nicknamevalidator;
import com.studyolle.studyolle.settings.validator.PasswordFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SettingsController {
	
	static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
	static final String SETTINGS_PROFILE_URL="/settings/profile";
	
	static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
	static final String SETTINGS_PASSWORD_URL="/settings/password";
	
	static final String SETTINGS_NOTIFICATION_VIEW_NAME = "/settings/notifications";
	static final String SETTINGS_NOTIFICATION_URL="/settings/notifications";

	static final String SETTINGS_ACCOUNT_VIEW_NAME = "/settings/account";
	static final String SETTINGS_ACCOUNT_URL="/settings/account";
	
	
	private final AccountService accountService;
	private final ModelMapper modelMapper;
	
	private final Nicknamevalidator nicknameValidator;
	
	
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
}
