package com.crimsonlogic.eventmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.eventmanagement.entity.UserDetails;
import com.crimsonlogic.eventmanagement.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String>{

	Wallet findByWalletForUser(UserDetails user); 

}
