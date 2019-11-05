package com.db.awmd.challenge.domain.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountDto {
	
	private String accountFromId;
	
	private String accountToId;
	
	private BigDecimal amount;

}
