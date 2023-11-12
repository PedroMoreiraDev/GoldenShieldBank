package com.goldenshieldbank.utils.constants;

public class AppConstants {
    public static final String ACTIVE = "ACTIVE";



    public interface MESSAGE {

        public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";


        public static final String ACCOUNT_CRATION_MESSAGE = "Account has been successfully created!";

        public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number does not exist";

        public static final String ACCOUNT_FOUND_SUCCESS_MESSAGE = "User Account Found";

        public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account was credited successfully";

        public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";

        public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited";



    }


    public interface CODE {

        public static final String ACCOUNT_EXISTS_CODE = "001";


        public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";

        public static final String ACCOUNT_NOT_EXIST_CODE = "002";

        public static final String ACCOUNT_FOUND_CODE = "004";

        public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";

        public static final String INSUFFICIENT_BALANCE_CODE = "006";

        public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";


    }

}
