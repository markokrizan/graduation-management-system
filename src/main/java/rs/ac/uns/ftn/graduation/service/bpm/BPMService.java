package rs.ac.uns.ftn.graduation.service.bpm;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.graduation.model.User;
import rs.ac.uns.ftn.graduation.payload.response.TaskPayload;
import rs.ac.uns.ftn.graduation.repository.UserRepository;

@Service
public class BPMService extends BaseBPMService {

    @Autowired
    UserRepository userRepository;

    public TaskPayload getNextTask(User user) {
        ProcessInstance process = startOrGetProcess(user.getCurrentProcessId());

        if(user.getCurrentProcessId() == null) {
            user.setCurrentProcessId(process.getId());
            userRepository.save(user);
        }

        Task task = getCurrentTask(process.getId());
        
        TaskPayload taskPayload = new TaskPayload();
        taskPayload.setProcessInstanceId(process.getProcessDefinitionId());
        taskPayload.setId(task.getId());
        taskPayload.setName(task.getName());
        taskPayload.setAssignee(user);
        taskPayload.setFormFields(getTaskFormFields(task.getId()));

        return taskPayload;
    }

    public void completeTask() {

    }

}