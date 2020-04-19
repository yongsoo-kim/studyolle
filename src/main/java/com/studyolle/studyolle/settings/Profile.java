package com.studyolle.studyolle.settings;

import org.hibernate.validator.constraints.Length;

import com.studyolle.studyolle.domain.Account;

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
	
	public Profile(Account account) {
		this.bio = account.getBio();
		this.url = account.getUrl();
		this.occupation = account.getOccupation();
		this.location = account.getLocation();
		this.profileImage = account.getProfileImage();
	}
}
