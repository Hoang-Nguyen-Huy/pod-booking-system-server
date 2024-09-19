package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.AccountCreationRequest;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    private AccountRepository accountRepository;
    AccountMapper accountMapper;

    public Account createAccount(AccountCreationRequest request) {
        Account account = accountMapper.toAccount(request);
        return accountRepository.save(account);
    }

    public List<Account> getAccounts(){
        return accountRepository.findAll();
    }
}
