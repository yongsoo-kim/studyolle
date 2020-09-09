package com.studyolle.studyolle.modules.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.studyolle.studyolle.infra.config.AppProperties;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import com.studyolle.studyolle.infra.mail.EmailMessage;
import com.studyolle.studyolle.infra.mail.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

import com.studyolle.studyolle.modules.account.form.Notifications;
import com.studyolle.studyolle.modules.account.form.Profile;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final TemplateEngine templateEngine;
	private final AppProperties appProperties;
	
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
		Context context = new Context();
		context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
		context.setVariable("nickname", newAccount.getNickname());
		context.setVariable("linkName", "이메일 인증하기");
		context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
		context.setVariable("host", appProperties.getHost());

		String message = templateEngine.process("mail/simple-link", context);


		EmailMessage emailMessage = EmailMessage.builder()
				.to(newAccount.getEmail())
				.subject("스터디올래, 회원 가입 인증")
				.message(message)
				.build();

		emailService.sendEmail(emailMessage);
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

		Context context = new Context();
		context.setVariable("link", "/login-by-email?token="+account.getEmailCheckToken()+"&email="+account.getEmail());
		context.setVariable("nickname", account.getNickname());
		context.setVariable("linkName", "스터디올래 로그인하기");
		context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
		context.setVariable("host", appProperties.getHost());

		String message = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
				.to(account.getEmail())
				.subject("스터디 올래, 로그인 링크")
				.message(message)
				.build();
		emailService.sendEmail(emailMessage);
	}

    public void addTag(Account account, Tag tag) {
		// Here,  'account' is now in 'detached' status.
		// And... **toMany can't use Lazy loading. -> 'Tag' will be null!
		// ** TIP**
		// Only 'persist' status can make us use Lazy loading again -> we need to change the status.
		Optional<Account> byId = accountRepository.findById(account.getId()); // 'findById()' is Eager loading. And  'accountRepository.getOne()' is Lazy loading.
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

	public Account getAccount(String nickname) {

		Account account = accountRepository.findByNickname(nickname);
		if (account == null) {
			throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
		}
		return  account;
	}
}
