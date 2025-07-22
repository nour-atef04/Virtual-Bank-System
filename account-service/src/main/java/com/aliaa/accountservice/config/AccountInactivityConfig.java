package com.aliaa.accountservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "account.inactivity")
@Data
public class AccountInactivityConfig {
    private int thresholdMinutes = 1440;
    private int checkIntervalMinutes = 5;
}
