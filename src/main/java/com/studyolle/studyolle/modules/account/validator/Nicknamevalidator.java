package com.studyolle.studyolle.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.form.NicknameForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Nicknamevalidator implements Validator {

	private final AccountRepository accountRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return NicknameForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		NicknameForm nicknameForm = (NicknameForm) target;
		Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
		
		if (byNickname != null) {
			errors.rejectValue("nickname", "wrong.value", "이미 사용중인 닉네임이므로, 입력하신 닉네임을 사용할수 없습니다.");
		}

	}

}
