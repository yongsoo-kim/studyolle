package com.studyolle.studyolle.main;

import com.studyolle.studyolle.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final AccountRepository accountRepository;
	
	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if (account != null) {
			Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
			model.addAttribute(accountLoaded);
		}
		
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}


}
