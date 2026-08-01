package com.siddu.dto.transfer.Response;

import java.util.UUID;

public record AccountTransferParty(

        UUID accountId,
        String AccountNumber,
        String accountHolderName

) {}