package com.studyolle.studyolle.modules.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

//By adding this, this repo will not update any info in DB
//This will give us some performance benefit.
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Account findByEmail(String email);

	Account findByNickname(String emailOrNickName);

	@EntityGraph(attributePaths = {"tags", "zones"})
	Account findAccountWithTagsAndZonesById(Long id);
}
