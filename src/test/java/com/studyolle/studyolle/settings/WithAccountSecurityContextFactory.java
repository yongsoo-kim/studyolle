package com.studyolle.studyolle.settings;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.SignUpForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount>{
	
	private final AccountService accountService;
	

	@Override
	public SecurityContext createSecurityContext(WithAccount withAccount) {
		
		String nickname = withAccount.value();
		
		//Create account info first
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname(nickname);
		signUpForm.setEmail("smilesigma@gmail.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
		
		
		//Get Security context

		
		UserDetails principal = accountService.loadUserByUsername(nickname);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);

		return context;
	}

}
