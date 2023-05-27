package com.gawpshardware.onlinestore.service;

import com.gawpshardware.onlinestore.api.model.LoginBody;
import com.gawpshardware.onlinestore.api.model.RegistrationBody;
import com.gawpshardware.onlinestore.api.model.VerificationToken;
import com.gawpshardware.onlinestore.exception.EmailFailureException;
import com.gawpshardware.onlinestore.exception.UserAlreadyExistsException;
import com.gawpshardware.onlinestore.exception.UserNotVerifiedException;
import com.gawpshardware.onlinestore.model.LocalUser;
import com.gawpshardware.onlinestore.model.dao.LocalUserDAO;

import com.gawpshardware.onlinestore.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class  UserService {

    private LocalUserDAO localUserDAO;
    private VerificationTokenDAO verificationTokenDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;

    public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService,
                       EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDAO = localUserDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        LocalUser saved = localUserDAO.save(user);

        VerificationToken verificationToken = createVerificationToken(saved);
        emailService.sendVerificationEmail(verificationToken);
        verificationTokenDAO.save(verificationToken);

        return saved;
    }

    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser (LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opUser.isPresent()){
            LocalUser user = opUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if (user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                } else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 ||
                            verificationTokens.get(0).getCreatedTimeStamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if (resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);

                }

            }
        }
        return null;

    }
    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if (opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if (!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }
}
