package com.example.desporto24.repository.payment;

import com.example.desporto24.model.payment.StripeConnectedAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StripeConnectedAccountRepository extends JpaRepository<StripeConnectedAccount, String> {
    Optional<StripeConnectedAccount> findByUsernameIgnoreCase(String username);
    Optional<StripeConnectedAccount> findByStripeAccountId(String stripeAccountId);
}
