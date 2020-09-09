package com.studyolle.studyolle.modules.account.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class NicknameForm {
	@NotBlank
	@Length(min = 3, max = 20)
	@Pattern(regexp ="^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
	private String nickname;
}
