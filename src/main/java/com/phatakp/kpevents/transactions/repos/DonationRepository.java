package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.transactions.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, String> {
}