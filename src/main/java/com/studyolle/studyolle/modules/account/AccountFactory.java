package com.studyolle.studyolle.modules.account;

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
        account.setEmail(nickName + "@email.com");
        accountRepository.save(account);
        return account;
    }
}
