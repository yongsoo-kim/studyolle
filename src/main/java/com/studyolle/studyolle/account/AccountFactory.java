package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String nickName) {
        Account account = new Account();
        account.setNickname(nickName);
        account.setEmail("yongsookim.com@gmail.com");
        accountRepository.save(account);
        return account;
    }
}
