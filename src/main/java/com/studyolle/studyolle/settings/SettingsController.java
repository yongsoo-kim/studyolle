package com.studyolle.studyolle.settings;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	private static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
	private static final String SETTINGS_PROFILE_URL="/settings/profile";
	
	private final AccountService accountService;

	
	@GetMapping(SETTINGS_PROFILE_URL)
	public String profileUpdateForm(@CurrentUser Account account, Model model) {
		
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
}
