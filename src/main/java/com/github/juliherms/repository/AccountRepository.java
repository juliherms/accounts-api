package com.github.juliherms.repository;

import com.github.juliherms.model.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

/**
 * This class responsible to access Account entity
 */
@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {

    /**
     * Find account by accountNumber
     * @param accountNumber
     * @return
     */
    public Account findByAccountNumber(Long accountNumber) {
        return find("accountNumber = ?1", accountNumber).firstResult();
    }
}
