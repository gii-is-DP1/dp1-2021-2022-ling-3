/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.parchisoca.user;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Mostly used as a facade for all Petclinic controllers Also a placeholder
 * for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 */
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.verificationTokenService = verificationTokenService;
    }

    //used for saving new user and updating existing user
    @Transactional
    public void saveUser(User user) throws DataAccessException {
        user.setRole(UserRole.PLAYER);
        if(!findUser(user.username).isPresent()) {
            user.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        }
        userRepository.save(user);
    }

    void confirmUser(VerificationToken confirmationToken) {
        final User user = confirmationToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        verificationTokenService.deleteVerificationToken(confirmationToken.getId());
    }

    public Optional < User > getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println("current user: " + currentPrincipalName);
        return findUser(currentPrincipalName);
    }

    public Optional < User > findUser(String username) {
        return userRepository.findById(username);
    }

    public List < User > findAllUsersWithEmail() {
        return userRepository.findByEmailNotNull();
    }

    public List < User > findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    public User getSelectedUser(String username) {
        return userRepository.findByUsername(username);
    }
}
