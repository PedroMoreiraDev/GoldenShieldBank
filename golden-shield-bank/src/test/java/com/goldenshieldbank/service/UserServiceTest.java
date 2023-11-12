package com.goldenshieldbank.service;

import com.goldenshieldbank.entity.User;
import com.goldenshieldbank.entity.dto.*;
import com.goldenshieldbank.repository.UserRepository;
import com.goldenshieldbank.service.impl.UserServiceImpl;
import com.goldenshieldbank.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.goldenshieldbank.utils.constants.AppConstants.CODE.*;
import static com.goldenshieldbank.utils.constants.AppConstants.MESSAGE.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {


    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create an account when everything is OK")
     void testCreateAccountSuccess() {
        UserRequest userRequest = buildUserRequest();

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(buildSavedUser());

        BankResponse bankResponse = userServiceImpl.createAccount(userRequest);

        assertEquals(ACCOUNT_CREATION_SUCCESS_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_CRATION_MESSAGE, bankResponse.getResponseMessage());
        assertNotNull(bankResponse.getAccountInfo());
        assertNotNull(bankResponse.getAccountInfo().getAccountBalance());
        assertNotNull(bankResponse.getAccountInfo().getAccountNumber());
        assertNotNull(bankResponse.getAccountInfo().getAccountName());
    }

    @Test
    @DisplayName("Should throw a error message and should not create an account ")
     void testCreateAccountWithExistingEmail() {

        UserRequest userRequest = buildUserRequest();

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        BankResponse bankResponse = userServiceImpl.createAccount(userRequest);

        assertEquals(ACCOUNT_EXISTS_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_EXISTS_MESSAGE, bankResponse.getResponseMessage());
        assertNull(bankResponse.getAccountInfo());
    }

    @Test
    @DisplayName("Should send an email when the account is created ")
     void testSendEmailUserCreated() {
        User savedUser = buildSavedUser();
        userServiceImpl.sendEmailUserCreated(savedUser);
        assertTrue(true);
    }



    @Test
    @DisplayName("Should search and return balance enquiry")
    void testBalanceEnquiryAccountExists() {
        EnquiryRequest enquiryRequest = buildEnquiryRequest();
        when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        User foundUser = buildSavedUser();
        when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(foundUser);

        BankResponse bankResponse = userServiceImpl.balanceEnquiry(enquiryRequest);

        assertEquals(ACCOUNT_FOUND_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_FOUND_SUCCESS_MESSAGE, bankResponse.getResponseMessage());
        assertNotNull(bankResponse.getAccountInfo());
        assertNotNull(bankResponse.getAccountInfo().getAccountBalance());
        assertNotNull(bankResponse.getAccountInfo().getAccountNumber());
        assertNotNull(bankResponse.getAccountInfo().getAccountName());
        assertEquals(bankResponse.getAccountInfo().getAccountBalance(), new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should return that the account does not exist")
    void testExistsByAccountNumber() {
        EnquiryRequest enquiryRequest = buildEnquiryRequest();
        when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(false);

        BankResponse bankResponse = userServiceImpl.balanceEnquiry(enquiryRequest);

        assertEquals(ACCOUNT_NOT_EXIST_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_NOT_EXIST_MESSAGE, bankResponse.getResponseMessage());
        assertNull(bankResponse.getAccountInfo());
    }

    @Test
    @DisplayName("Should search and return the account name ")
    void testNameEnquiryAccountExists() {
        EnquiryRequest enquiryRequest = buildEnquiryRequest();
        when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        User foundUser = buildSavedUser();
        when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(foundUser);

        String accountName = userServiceImpl.nameEnquiry(enquiryRequest);

        assertEquals("John Smith Doe", accountName);
    }

    @Test
    @DisplayName("Should credit the amount to the account balance ")
    void testCreditAccountSuccess() {

        CreditDebitRequest creditDebitRequest = buildCreditDebitRequest(); //Credit amount 15
        when(userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(true);
        User userToCredit = buildSavedUser(); //user created  20 amount
        when(userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(userToCredit);

        BankResponse bankResponse = userServiceImpl.creditAccount(creditDebitRequest);

        assertEquals(ACCOUNT_CREDITED_SUCCESS_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_CREDITED_SUCCESS_MESSAGE, bankResponse.getResponseMessage());
        assertNotNull(bankResponse.getAccountInfo());
        assertNotNull(bankResponse.getAccountInfo().getAccountBalance());
        assertNotNull(bankResponse.getAccountInfo().getAccountNumber());
        assertNotNull(bankResponse.getAccountInfo().getAccountName());
        assertEquals(new BigDecimal("35.00"), bankResponse.getAccountInfo().getAccountBalance()); // 20 amount in account + 15 amount (increased) = 35 amont
    }

    @Test
    @DisplayName("Should debit the amount to the account balance ")
    void tesDebitAccountSuccess() {

        CreditDebitRequest creditDebitRequest = buildCreditDebitRequest(); //Credit amount 15
        when(userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(true);
        User userToDebit = buildSavedUser(); //user debited created - 20 amount
        when(userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(userToDebit);

        BankResponse bankResponse = userServiceImpl.debitAccount(creditDebitRequest);

        assertEquals(ACCOUNT_DEBITED_SUCCESS_CODE, bankResponse.getResponseCode());
        assertEquals(ACCOUNT_DEBITED_SUCCESS_MESSAGE, bankResponse.getResponseMessage());
        assertNotNull(bankResponse.getAccountInfo());
        assertNotNull(bankResponse.getAccountInfo().getAccountBalance());
        assertNotNull(bankResponse.getAccountInfo().getAccountNumber());
        assertNotNull(bankResponse.getAccountInfo().getAccountName());
        assertEquals(new BigDecimal("5.00"), bankResponse.getAccountInfo().getAccountBalance()); // 20 amount in account - 15 amount (decreased) = 5 amont
    }

    @Test
    @DisplayName("Should return the insufficient balance message for debit ")
    void testDebitAccountInsuficientBalance() {

        CreditDebitRequest creditDebitRequest = buildCreditDebitRequest(); //Debit amount 15
        when(userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(true);
        User userToDebit = buildSavedUser(); //user debited created 20 amount
        when(userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber())).thenReturn(userToDebit);

        userToDebit.setAccountBalance(new BigDecimal("1.00")); // set new amount = 1

        BankResponse bankResponse = userServiceImpl.debitAccount(creditDebitRequest);

        assertNull(bankResponse.getAccountInfo());
        assertEquals(INSUFFICIENT_BALANCE_CODE, bankResponse.getResponseCode());
        assertEquals(INSUFFICIENT_BALANCE_MESSAGE, bankResponse.getResponseMessage()); //  1.00 amount in account - 15 amount (decreased) = ERROR MSG

    }

    private UserRequest buildUserRequest() {
        return UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .otherName("Smith")
                .gender("Male")
                .address("123 Main St")
                .stateOfOrigin("SomeState")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .alternativePhoneNumber("0987654321")
                .build();
    }

    private User buildSavedUser() {
        return User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .otherName("Smith")
                .gender("Male")
                .address("123 Main St")
                .stateOfOrigin("SomeState")
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(new BigDecimal("20.00"))
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .alternativePhoneNumber("0987654321")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusDays(1))
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    private EnquiryRequest buildEnquiryRequest(){
        return EnquiryRequest.builder().accountNumber(AccountUtils.generateAccountNumber()).build();
    }

    private CreditDebitRequest buildCreditDebitRequest() {
        return CreditDebitRequest.builder()
                .accountNumber(AccountUtils.generateAccountNumber())
                .amount(new BigDecimal("15.00"))
                .build();

    }


}