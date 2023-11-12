package com.goldenshieldbank.service;

import com.goldenshieldbank.entity.dto.BankResponse;
import com.goldenshieldbank.entity.dto.CreditDebitRequest;
import com.goldenshieldbank.entity.dto.EnquiryRequest;
import com.goldenshieldbank.entity.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

    String nameEnquiry(EnquiryRequest enquiryRequest);
    
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);

    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);



}
