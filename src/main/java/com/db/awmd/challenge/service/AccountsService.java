package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.dto.AccountDto;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.repository.AccountsRepository;

@Service
public class AccountsService {

	@Autowired
	private final AccountsRepository accountsRepository;

	@Autowired
	private NotificationService emailNotificationService;
	
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public void transferAmount(AccountDto accountDto) throws InvalidAccountException {
		Account accountFromId = this.accountsRepository.isExist(accountDto.getAccountFromId());
		if (accountFromId == null) {
			throw new InvalidAccountException("Account is does not exist. Invlaid accountFromId");
		}
		Account accountToId = this.accountsRepository.isExist(accountDto.getAccountToId());

		if (accountToId == null) {
			throw new InvalidAccountException("Account is does not exist. Invlaid accountToId");
		}
		if (accountDto.getAmount().compareTo(BigDecimal.ZERO) == -1) {
			throw new InvalidAccountException("Amount should be a positive number");
		}
		if (accountFromId.getBalance().compareTo(accountDto.getAmount()) < 0) {
			throw new InvalidAccountException("Insufficient fund");
		}

		accountFromId.setBalance(accountFromId.getBalance().subtract(accountDto.getAmount()));
		accountToId.setBalance(accountToId.getBalance().add(accountDto.getAmount()));
		this.accountsRepository.updateAccountBalance(accountFromId);
		this.accountsRepository.updateAccountBalance(accountToId);

		// Notification Service call
		emailNotificationService.notifyAboutTransfer(accountFromId,
				accountDto.getAmount() + " transfered to " + accountToId.getAccountId());
		emailNotificationService.notifyAboutTransfer(accountToId,
				accountDto.getAmount() + " received from " + accountFromId.getAccountId());

	}

}
