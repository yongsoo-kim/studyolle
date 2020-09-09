package com.studyolle.studyolle.modules.account.validator;

import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> clazz) {

		return clazz.isAssignableFrom(SignUpForm.class);
	}

	@Override
	public void validate(Object object, Errors errors) {

		SignUpForm signUpForm = (SignUpForm) object;
		if (accountRepository.existsByEmail(signUpForm.getEmail())) {
			errors.rejectValue("email", "invalid.email", new Object[] { signUpForm.getEmail() }, "이미 사용중인 이메일입니다.");
		}

		if (accountRepository.existsByNickname(signUpForm.getNickname())) {
			errors.rejectValue("nickname", "invalid.nickname", new Object[] { signUpForm.getEmail() }, "이미 사용중인 닉네임입니다.");
		}
	}

}
