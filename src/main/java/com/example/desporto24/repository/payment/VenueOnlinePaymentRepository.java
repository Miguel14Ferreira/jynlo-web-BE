package com.example.desporto24.repository.payment;

import com.example.desporto24.model.payment.VenueOnlinePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueOnlinePaymentRepository extends JpaRepository<VenueOnlinePayment, String> {
}
