package com.helios.seata.account.service;

import com.helios.seata.account.persistence.Account;
import com.helios.seata.account.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private static final String ERROR_USER_ID = "1002";

    @Autowired
    private AccountMapper accountMapper;


    @Transactional(rollbackFor = Exception.class)
    public void debit(String userId, BigDecimal num) {
        Account account = accountMapper.findByUserId(userId);
        account.setMoney(account.getMoney().subtract(num));
        accountMapper.updateById(account);

        if (ERROR_USER_ID.equals(userId)) {
            throw new RuntimeException("account branch exception");
        }
    }

}
