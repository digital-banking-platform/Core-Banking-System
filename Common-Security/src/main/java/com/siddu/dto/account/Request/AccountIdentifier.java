package com.siddu.dto.account.Request;

import java.util.UUID;

public record AccountIdentifier (
    UUID userId,
    String AccountNumber)
    {}

