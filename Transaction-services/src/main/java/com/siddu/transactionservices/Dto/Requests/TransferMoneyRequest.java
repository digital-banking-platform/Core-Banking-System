package com.siddu.transactionservices.Dto.Requests;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;


public record TransferMoneyRequest(

        @NotBlank(message = "Sender account number required")
        @Pattern(
                regexp = "\\d{12}",
                message = "Sender account number must be exactly 12 digits"
        )
        String senderAccountNumber,


        @NotBlank(message = "Receiver account number required")
        @Pattern(
                regexp = "\\d{12}",
                message = "Receiver account number must be exactly 12 digits"
        )
        String receiverAccountNumber,


        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "1.00",
                message = "Amount must be greater than 0"
        )
        BigDecimal amount,


        @NotBlank(message = "Transaction PIN required")
        @Pattern(
                regexp = "\\d{6}",
                message = "Transaction PIN must be 6 digits"
        )
        String transactionPin,

        @Size(
                max = 255,
                message = "Description cannot exceed 255 characters"
        )
        String description,

        @NotNull(message = "unique key is required")
        UUID idempotencyKey
) {}