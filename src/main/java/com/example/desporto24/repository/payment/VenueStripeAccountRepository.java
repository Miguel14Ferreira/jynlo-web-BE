package com.example.desporto24.repository.payment;

import com.example.desporto24.model.payment.VenueStripeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueStripeAccountRepository extends JpaRepository<VenueStripeAccount, Long> {
    Optional<VenueStripeAccount> findByStripeAccountId(String stripeAccountId);
}
