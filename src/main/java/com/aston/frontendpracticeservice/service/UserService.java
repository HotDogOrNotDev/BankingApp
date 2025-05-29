package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.domain.entity.User;

public interface UserService {

    User findByLogin(String login);

}
