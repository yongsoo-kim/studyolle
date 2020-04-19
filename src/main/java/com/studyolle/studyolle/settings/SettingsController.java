package com.studyolle.studyolle.settings;

import javax.validation.Valid;

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

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SettingsController {
	
	static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
	static final String SETTINGS_PROFILE_URL="/settings/profile";
	
	static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
	static final String SETTINGS_PASSWORD_URL="/settings/password";
	
	private final AccountService accountService;
	
	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidator());
	}
	
	@GetMapping(SETTINGS_PROFILE_URL)
	public String updateProfileForm(@CurrentUser Account account, Model model) {
		
		model.addAttribute("account", account);
		model.addAttribute(new Profile(account));
		
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
}
