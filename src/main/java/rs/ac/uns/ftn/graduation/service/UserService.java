package rs.ac.uns.ftn.graduation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.graduation.exception.AppException;
import rs.ac.uns.ftn.graduation.model.User;
import rs.ac.uns.ftn.graduation.repository.UserRepository;
import rs.ac.uns.ftn.graduation.security.UserPrincipal;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public User getUserFromPrincipal(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new AppException("Principal user does not exist!"));
    }
        

}