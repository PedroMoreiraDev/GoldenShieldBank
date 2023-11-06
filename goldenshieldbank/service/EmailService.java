package com.goldenshieldbank.service;

import com.goldenshieldbank.entity.dto.EmailDetails;

public interface EmailService {

    void senderEmailAlert(EmailDetails emailDetails);

}
