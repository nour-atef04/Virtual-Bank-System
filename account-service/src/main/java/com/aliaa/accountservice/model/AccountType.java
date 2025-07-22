package com.aliaa.accountservice.model;

public enum AccountType {
    SAVINGS,
    CHECKING;

    public static boolean isValidType(AccountType type) {
        if (type == null) return false;
        for (AccountType validType : AccountType.values()) {
            if (validType == type) {
                return true;
            }
        }
        return false;
        }
    }


