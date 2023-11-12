package com.goldenshieldbank.service.impl;

import com.goldenshieldbank.entity.User;
import com.goldenshieldbank.entity.dto.*;
import com.goldenshieldbank.repository.UserRepository;
import com.goldenshieldbank.service.EmailService;
import com.goldenshieldbank.service.UserService;
import com.goldenshieldbank.utils.constants.AppConstants;
import com.goldenshieldbank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.goldenshieldbank.utils.constants.AppConstants.CODE.*;
import static com.goldenshieldbank.utils.constants.AppConstants.MESSAGE.*;

@Service

public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_EXISTS_CODE)
                    .responseMessage(ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status(AppConstants.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);

        this.sendEmailUserCreated(savedUser);

        return BankResponse.builder()
                .responseCode(ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(ACCOUNT_CRATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName()).build())
                .build();
    }

    public void sendEmailUserCreated(User savedUser) {
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Parabéns! Sua conte foi criada com sucesso.  \nOs detalhes de sua conta: " +
                        "Nome da conta: " + savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName()
                        + "\n Numero da conta: " + savedUser.getAccountNumber())
                .build();
        emailService.senderEmailAlert(emailDetails);

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(ACCOUNT_FOUND_CODE)
                .responseMessage(ACCOUNT_FOUND_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if (!isAccountExist) {
            return ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName();


    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());

            BigDecimal requestAmount = creditDebitRequest.getAmount();
            BigDecimal accountBalance = userToCredit.getAccountBalance();

            userToCredit.setAccountBalance(accountBalance.add(requestAmount));

            userRepository.save(userToCredit);

            return BankResponse.builder()
                    .responseCode(ACCOUNT_CREDITED_SUCCESS_CODE)
                    .responseMessage(ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToCredit.getAccountBalance())
                            .accountNumber(userToCredit.getAccountNumber())
                            .accountName(userToCredit.getFirstName() + " " + userToCredit.getOtherName() + " " + userToCredit.getLastName())
                            .build())
                    .build();

    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());

            BigDecimal requestAmount = creditDebitRequest.getAmount();
            BigDecimal accountBalance = userToDebit.getAccountBalance();

            if (requestAmount.compareTo(accountBalance) <= 0) {
                userToDebit.setAccountBalance(accountBalance.subtract(requestAmount));
                userRepository.save(userToDebit);
                return BankResponse.builder()
                        .responseCode(ACCOUNT_DEBITED_SUCCESS_CODE)
                        .responseMessage(ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(creditDebitRequest.getAccountNumber())
                                .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                                .accountBalance(userToDebit.getAccountBalance())
                                .build())
                        .build();
            } else {
                return BankResponse.builder()
                        .responseCode(INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
        }
}

