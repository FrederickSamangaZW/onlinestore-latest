package com.gawpshardware.onlinestore.model.dao;

import com.gawpshardware.onlinestore.api.model.VerificationToken;
import com.gawpshardware.onlinestore.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);

}
