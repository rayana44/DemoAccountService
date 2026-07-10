package com.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardApplicationRequest {
    private String accountNumber;
    private String customerName;
    private String email;
    private String mobileNumber;
    private Integer cibilScore;
    private String cardType;
    private String requestReason;
    private LocalDateTime applicationDate;
}
