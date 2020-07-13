package rs.ac.uns.ftn.graduation.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.graduation.payload.response.TaskPayload;
import rs.ac.uns.ftn.graduation.security.CurrentUser;
import rs.ac.uns.ftn.graduation.security.UserPrincipal;
import rs.ac.uns.ftn.graduation.service.UserService;
import rs.ac.uns.ftn.graduation.service.bpm.BPMService;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private BPMService bpmService;

    @Autowired
    private UserService userService;

    @GetMapping("/task")
    @PreAuthorize("hasRole('USER')")
    public TaskPayload getNextTask(@CurrentUser UserPrincipal currentUser) {
        return bpmService.getNextTask(userService.getUserFromPrincipal(currentUser));
    }

    @PostMapping("/task/complete")
    @PreAuthorize("hasRole('USER')")
    public TaskPayload completeTask(@RequestBody HashMap<String, Object> formFields, @CurrentUser UserPrincipal currentUser) {
        return bpmService.completeTask(userService.getUserFromPrincipal(currentUser), formFields);
    }
}
