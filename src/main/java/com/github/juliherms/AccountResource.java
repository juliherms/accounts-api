package com.github.juliherms;

import com.github.juliherms.model.Account;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Path("/accounts")
public class AccountResource {

    Set<Account> accounts = new HashSet<>();

    @PostConstruct
    public void setup() {
        accounts.add(new Account(123456789L, 987654321L, "George Baird", new BigDecimal("354.23")));
        accounts.add(new Account(121212121L, 888777666L, "Mary Taylor", new BigDecimal("560.03")));
        accounts.add(new Account(545454545L, 222444999L, "Diana Rigg", new BigDecimal("422.00")));
    }

    /**
     * Method responsible to create account
     * @param account
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        if (account.getAccountNumber() == null) {
            throw new WebApplicationException("No Account number specified.", 400);
        }

        accounts.add(account);
        return Response.status(201).entity(account).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Account> allAccounts() {
        return accounts;
    }

    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber){
        Optional<Account> response =
                accounts.stream().filter(
                        acct -> acct.getAccountNumber().equals(accountNumber))
                        .findFirst();

        return response.orElseThrow(() -> new NotFoundException("Account with id of " + accountNumber + "not found."));
    }

    @PUT
    @Path("{accountNumber}/withdrawal")
    public Account withdrawal(@PathParam("accountNumber") Long accountNumber, String amount) {
        Account account = getAccount(accountNumber);
        account.withdrawFunds(new BigDecimal(amount));
        return account;
    }
}
