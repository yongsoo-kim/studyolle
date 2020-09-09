package com.studyolle.studyolle.infra.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public ModelMapper modelMapper() {
		
// Without this setting, you will see this error.		
//		1) The destination property com.studyolle.studyolle.module.account.Account.setEmail() matches multiple source property hierarchies:
//
//			com.studyolle.studyolle.module.settings.Notifications.isStudyEnrollmentResultByEmail()
//			com.studyolle.studyolle.module.settings.Notifications.isStudyUpdatedByEmail()
//			com.studyolle.studyolle.module.settings.Notifications.isStudyCreatedByEmail()
		
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
		.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
		.setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
		return modelMapper;
	}

}
