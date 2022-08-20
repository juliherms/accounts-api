package com.github.juliherms.resources;

import com.github.juliherms.model.Account;
import com.github.juliherms.model.AccountStatus;
import com.github.juliherms.repository.AccountRepository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountRepository accountRepository;

    /**
     * Method responsible to create account
     * @param account
     * @return
     */
    @POST
    @Transactional
    public Response createAccount(Account account) {

        if (account.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 400);
        }

        accountRepository.persist(account);
        return Response.status(201).entity(account).build();
    }

    @GET
    public List<Account> allAccounts() {
        return accountRepository.listAll();
    }

    @GET
    @Path("/{accountNumber}")
    public Account getAccount(@PathParam("accountNumber") Long accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
        }

        return account;
    }

    /**
     * Method responsible to withdrawal account
     * @param accountNumber
     * @param amount
     * @return
     */
    @PUT
    @Path("{accountNumber}/withdrawal")
    public Account withdrawal(@PathParam("accountNumber") Long accountNumber, String amount) {

        Account entity = accountRepository.findByAccountNumber(accountNumber);

        if (entity == null) {
            throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
        }

        if (entity.getAccountStatus().equals(AccountStatus.OVERDRAWN)) {
            throw new WebApplicationException("Account is overdrawn, no further withdrawals permitted", 409);
        }

        entity.withdrawFunds(new BigDecimal(amount));

        return entity;
    }

    /**
     * Method responsible to deposit funds in the account
     * @param accountNumber
     * @param amount
     * @return
     */
    @PUT
    @Path("{accountNumber}/deposit")
    @Transactional
    public Account deposit(@PathParam("accountNumber") Long accountNumber, String amount) {
        Account entity = accountRepository.findByAccountNumber(accountNumber);

        if (entity == null) {
            throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
        }

        entity.addFunds(new BigDecimal(amount));
        return entity;
    }

    /**
     * Method responsible to remove account
     * @param accountNumber
     * @return
     */
    @DELETE
    @Path("{accountNumber}")
    @Transactional
    public Response closeAccount(@PathParam("accountNumber") Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
        }

        account.close();
        return Response.noContent().build();
    }
}
