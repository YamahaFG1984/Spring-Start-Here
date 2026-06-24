package com.example.ssh_ch14_ex1;

import com.example.ssh_ch14_ex1.exceptions.AccountNotFoundException;
import com.example.ssh_ch14_ex1.model.Account;
import com.example.ssh_ch14_ex1.repositories.AccountRepository;
import com.example.ssh_ch14_ex1.services.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransferServiceWithAnnotationsUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    @DisplayName("""
            GIVEN a valid sender and receiver account
            WHEN transferMoney is called
            THEN the amounts are updated for both accounts
            """)
    public void transferMoneyHappyFlow() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setAmount(new BigDecimal("1000"));

        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setAmount(new BigDecimal("1000"));

        given(accountRepository.findById(1L)).willReturn(Optional.of(sender));
        given(accountRepository.findById(2L)).willReturn(Optional.of(receiver));

        transferService.transferMoney(1L, 2L, new BigDecimal("100"));

        verify(accountRepository).changeAmount(1L, new BigDecimal("900"));
        verify(accountRepository).changeAmount(2L, new BigDecimal("1100"));
    }

    @Test
    @DisplayName("""
            GIVEN a non-existent sender account
            WHEN transferMoney is called
            THEN AccountNotFoundException is thrown
            """)
    public void transferMoneySenderAccountNotFound() {
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transferService.transferMoney(1L, 2L, new BigDecimal("100")));

        verify(accountRepository, never()).changeAmount(anyLong(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("""
            GIVEN a non-existent receiver account
            WHEN transferMoney is called
            THEN AccountNotFoundException is thrown
            """)
    public void moneyTransferDestinationAccountNotFoundFlow() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setAmount(new BigDecimal("1000"));

        given(accountRepository.findById(1L)).willReturn(Optional.of(sender));
        given(accountRepository.findById(2L)).willReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transferService.transferMoney(1L, 2L, new BigDecimal("100")));

        verify(accountRepository, never()).changeAmount(anyLong(), any(BigDecimal.class));
    }
}
