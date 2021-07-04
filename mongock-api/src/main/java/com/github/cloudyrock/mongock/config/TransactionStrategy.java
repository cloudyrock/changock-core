package com.github.cloudyrock.mongock.config;


public enum TransactionStrategy {

    MIGRATION, CHANGE_LOG;

    public boolean isTransaction() {
        return this == MIGRATION || this == CHANGE_LOG;
    }

}
