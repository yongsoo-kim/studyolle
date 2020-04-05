package com.studyolle.studyolle.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.studyolle.domain.Account;

//By adding this, this repo will not update any info in DB
//This will give us some performance benefit.
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Account findByEmail(String email);
}
