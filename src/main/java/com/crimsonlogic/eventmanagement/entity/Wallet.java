package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
	
	@Id
	@Column(name = "wallet_id", length = 10)
	private String walletId;
	
	@Column(name = "amount")
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "wallet_for_user")
	private UserDetails walletForUser;

}
