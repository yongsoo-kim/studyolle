package com.studyolle.studyolle.account;

import java.util.List;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.studyolle.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;
	//private final AuthenticationManager authenticationManager;
	
	@Transactional
	public Account processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);

		return newAccount;
	}
	
	private Account saveNewAccount(SignUpForm signUpForm) {
		Account account = Account.builder()
				.email(signUpForm.getEmail())
				.nickname(signUpForm.getNickname())
				.password(passwordEncoder.encode(signUpForm.getPassword()))
				.studyCreatedByWeb(true)
				.studyEnrollmentResultByWeb(true)
				.studyUpdatedByWeb(true)
				.build();

		Account newAccount = accountRepository.save(account);
		return newAccount;
	}

	protected void sendSignUpConfirmEmail(Account newAccount) {

		SimpleMailMessage mailMassage = new SimpleMailMessage();
		mailMassage.setTo(newAccount.getEmail());
		mailMassage.setSubject("스터디 올래, 회원 가입 인증");
		mailMassage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
		javaMailSender.send(mailMassage);
	}

	public void login(Account processNewAccount) {
		// Originally, this is for 'AuthenticationManager' or 'AuthenticationProvider'
		// So this is the 'right way' to authenticate user from spring boot.
		
//		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//				processNewAccount.getNickname(),
//				processNewAccount.getPassword(),
//				List.of(new SimpleGrantedAuthority("ROLE_USER")));
//		Authentication authentication = authenticationManager.authenticate(token);
//		SecurityContext context = SecurityContextHolder.getContext();
//		context.setAuthentication(authentication);
		
		
		//But we are gonna use this logic, because we can't use 'plain' password for DB.
		//By skipping 'AuthenticatoinManager', we can use encrypted passsword. 
		//And actually, this logic flow is almost same as 'AuthenticatoinManager' without password part!
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				new UserAccount(processNewAccount),
				processNewAccount.getPassword(),
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
		
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(token);
	}

	@Override
	public UserDetails loadUserByUsername(String emailOrNickName) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(emailOrNickName);
		if (account == null) {
			account = accountRepository.findByNickname(emailOrNickName);
		}
		
		if (account == null) {
			throw new UsernameNotFoundException(emailOrNickName);
		}
		
		return new UserAccount(account);
	}

}
