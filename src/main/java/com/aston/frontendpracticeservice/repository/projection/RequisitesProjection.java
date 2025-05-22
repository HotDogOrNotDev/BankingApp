package com.aston.frontendpracticeservice.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface RequisitesProjection {

    @Value("#{target.user.firstName}")
    String getUserName();

    String getAccountNumber();

    String getKbk();

}
