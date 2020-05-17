package com.studyolle.studyolle.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String nickname;

	private String password;

	private boolean emailVerified;

	private String emailCheckToken;

	private LocalDateTime joinedAt;

	private String bio;

	private String url;

	private String occupation;

	private String location;

	@Lob @Basic(fetch = FetchType.EAGER)
	private String profileImage;

	private boolean studyCreatedByEmail;

	private boolean studyCreatedByWeb = true;

	private boolean studyEnrollmentResultByEmail;

	private boolean studyEnrollmentResultByWeb = true;

	private boolean studyUpdatedByEmail;

	private boolean studyUpdatedByWeb = true;

	private LocalDateTime emailCheckTokenGeneratedAt;

	@ManyToMany
	private Set<Tag> tags = new HashSet<>();

	@ManyToMany
	private Set<Zone> zones = new HashSet<>();

	public void generateEmailCheckToken() {
		this.emailCheckToken  = UUID.randomUUID().toString();
		this.emailCheckTokenGeneratedAt = LocalDateTime.now();
	}

	public void completeSignUp() {
		this.emailVerified =  true;
		this.joinedAt = LocalDateTime.now();
	}

	public boolean isValidToken(String token) {
		return this.getEmailCheckToken().equals(token);
	}

	public boolean canSendConfirmEmail() {
		return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
	}

}
