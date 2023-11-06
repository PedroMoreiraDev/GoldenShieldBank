package com.goldenshieldbank.service.impl;

import com.goldenshieldbank.entity.dto.BankResponse;
import com.goldenshieldbank.entity.dto.UserRequest;
import org.springframework.stereotype.Service;

public interface UserService {
    BankResponse createAcount(UserRequest userRequest);
}
