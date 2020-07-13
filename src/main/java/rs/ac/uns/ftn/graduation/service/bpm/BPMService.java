package rs.ac.uns.ftn.graduation.service.bpm;

import java.util.HashMap;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.graduation.exception.AppException;
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

        Task task = getCurrentOrFirstTask(process.getId());
        
        TaskPayload taskPayload = new TaskPayload();
        taskPayload.setProcessInstanceId(process.getProcessDefinitionId());
        taskPayload.setId(task.getId());
        taskPayload.setName(task.getName());
        taskPayload.setAssignee(user);
        taskPayload.setFormFields(getTaskFormFields(task.getId()));

        return taskPayload;
    }

    public TaskPayload completeTask(User user, HashMap<String, Object> formFields) {
        if(user.getCurrentProcessId() == null) {
            throw new AppException("This user hasn't started a process instance!");
        }

        ProcessInstance process = getRunningProcessInstanceById(user.getCurrentProcessId());

        if (process == null) {
            throw new AppException("Invalid process id!");
        }

        Task task = getCurrentTask(user.getCurrentProcessId());

        if (task == null) {
            throw new AppException("Error getting current task!");
        }

        submitFormOrCompleteTask(task, formFields);
       
        Task nextTask = getCurrentTask(user.getCurrentProcessId());

        if (nextTask == null) {
            user.setCurrentProcessId(null);
            userRepository.save(user);

            TaskPayload taskPayload = new TaskPayload();
            taskPayload.setName(task.getName());
            taskPayload.setIsLast(true);
            
            return taskPayload;
        }

        TaskPayload taskPayload = new TaskPayload();
        taskPayload.setProcessInstanceId(process.getProcessDefinitionId());
        taskPayload.setId(nextTask.getId());
        taskPayload.setName(nextTask.getName());
        taskPayload.setAssignee(user);
        taskPayload.setFormFields(getTaskFormFields(nextTask.getId()));

        return taskPayload;
    }

    private void submitFormOrCompleteTask(Task task, HashMap<String, Object> formFields) {
        try {
            //Form submition completes the task as well
            if (taskHasFormFields(task.getId())) {   
                submitTaskForm(task.getId(), formFields);

                return;
            } 

            completeTask(task.getId());
        } catch (Exception e) {
            e.printStackTrace();

            throw new AppException("There has been an error while completing the task", e);
        }
    }
}
