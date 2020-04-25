package com.studyolle.studyolle.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.domain.Zone;
import org.modelmapper.ModelMapper;
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
import com.studyolle.studyolle.settings.form.Notifications;
import com.studyolle.studyolle.settings.form.Profile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	
	//private final AuthenticationManager authenticationManager;
	
	public Account processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		sendSignUpConfirmEmail(newAccount);

		return newAccount;
	}
	
	private Account saveNewAccount(SignUpForm signUpForm) {
		signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		Account account = modelMapper.map(signUpForm, Account.class);
		account.generateEmailCheckToken();
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

	@Transactional(readOnly = true)
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
	
	
	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);
	}

	public void updateProfile(Account account, Profile profile) {
		
		modelMapper.map(profile, account);
		//'account' object is detached here, so make it under control of 'Persistence Context'
		accountRepository.save(account);
	}

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passwordEncoder.encode(newPassword));
		accountRepository.save(account);
	}

	public void updateNotifications(Account account, Notifications notifications) {
		
		modelMapper.map(notifications, account);
		
		accountRepository.save(account);
	}

	public void updateNickName(Account account, String nickname) {
		account.setNickname(nickname);
		accountRepository.save(account);
		login(account);
	}

	public void sendLoginLink(Account account) {
		account.generateEmailCheckToken();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(account.getEmail());
		mailMessage.setSubject("스터디 올래, 로그인 링크");
		mailMessage.setText("/login-by-email?token="+account.getEmailCheckToken()+"&email="+account.getEmail());
		javaMailSender.send(mailMessage);
	}

    public void addTag(Account account, Tag tag) {
		// Here,  'account' is now in 'detached' status.
		// And... **toMany can't use Lazy loading. -> 'Tag' will be null!
		// ** TIP**
		// Only 'persist' status can make us use Lazy loading again -> we need to change the status.
		Optional<Account> byId = accountRepository.findById(account.getId()); // This is Eager loading. And  'accountRepository.getOne()' is Lazy loading.
		byId.ifPresent(a -> a.getTags().add(tag));
	}

	public Set<Tag> getTags(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getTags();
	}

	public void removeTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().remove(tag));
	}

	public void addZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().add(zone));
	}

	public Set<Zone> getZones(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getZones();
	}

	public void removeZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().remove(zone));
	}
}
