package com.goldenshieldbank.service.impl;

import com.goldenshieldbank.entity.User;
import com.goldenshieldbank.entity.dto.AccountInfo;
import com.goldenshieldbank.entity.dto.BankResponse;
import com.goldenshieldbank.entity.dto.UserRequest;
import com.goldenshieldbank.repository.UserRepository;
import com.goldenshieldbank.utils.constants.AppConstants;
import com.goldenshieldbank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.goldenshieldbank.utils.constants.AppConstants.*;

@Service

public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;



    @Override
    public BankResponse createAcount(UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())){
          return  BankResponse.builder()
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

        return BankResponse.builder()
                .responseCode(ACCOUNT_CREATION_SUCCESS)
                .responseMessage(ACCOUNT_CRATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName()).build())
                .build();
    }
}
