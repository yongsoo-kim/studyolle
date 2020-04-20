package com.studyolle.studyolle.settings.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
/*to Prevent null point exception.
 Modelattribute needs to have at least one constructor.
 If ' Profile(Account account)' is called, but 'account' is null, so we will get null point exception.
 So we need to have a constructor without any parameter for injecting Profile object with @ModelAttribute.
 *   
*/
@NoArgsConstructor
public class Profile {
	
	@Length(max = 35)
	private String bio;
	
	@Length(max = 50)
	private String url;
	
	@Length(max = 50)
	private String occupation;
	
	@Length(max = 50)
	private String location;
	
	private String profileImage;
}
