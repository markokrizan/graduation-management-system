package rs.ac.uns.ftn.graduation.controller;

import rs.ac.uns.ftn.graduation.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.graduation.model.User;
import rs.ac.uns.ftn.graduation.payload.*;
import rs.ac.uns.ftn.graduation.repository.UserRepository;
import rs.ac.uns.ftn.graduation.security.UserPrincipal;
import rs.ac.uns.ftn.graduation.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return userRepository.findById(currentUser.getId()).orElse(null);
    }

    @GetMapping("/users/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public User getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return user;
    }
}
