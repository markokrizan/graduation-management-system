package rs.ac.uns.ftn.graduation.service.bpm;

import java.util.HashMap;
import java.util.List;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseBPMService {

    protected static final String MAIN_PROCESS_KEY = "graduation-management-system-process";
    
    @Autowired
	private IdentityService identityService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
    private FormService formService;

    protected ProcessInstance startProcess(String processKey) {
        return runtimeService.startProcessInstanceByKey(processKey);
    }

    protected ProcessInstance getRunningProcessInstanceById(String processInstanceId) {
        return runtimeService
            .createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    }

    protected ProcessInstance startOrGetProcess(String processInstanceId) {
        if (processInstanceId == null) {
            return startProcess(MAIN_PROCESS_KEY);
        }

        return getRunningProcessInstanceById(processInstanceId);
    }

    protected List<Task> getProcessTasks(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    protected Task getProcessTask(String processInstanceId, String taskId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
    }

    protected Task getCurrentTask(String processInstanceId) {
        Task currentTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();

        if (currentTask == null) {
            return getProcessTasks(processInstanceId).get(0);
        }

        return currentTask;
    }

    protected List<FormField> getTaskFormFields(String taskId) {
        return formService.getTaskFormData(taskId).getFormFields();
    }

    protected void submitTaskForm(String taskId, HashMap<String, Object> formFieldMap) {
        formService.submitTaskForm(taskId, formFieldMap);
    }

    protected void setVariables(String processInstanceId, HashMap<String, Object> variableMap) {
        variableMap
            .entrySet()
            .stream()
            .forEach(mapEntry -> {
                String key = mapEntry.getKey();
                Object value = mapEntry.getValue();

                setVariable(processInstanceId, key, value);
            });
    }

    protected void setVariable(String processInstanceId, String key, Object value) {
        runtimeService.setVariable(processInstanceId, key, value);
    }

    protected void completeTask(String taskId) {
        taskService.complete(taskId);
    }

    protected User getUserById(String userId) {
        return identityService
            .createUserQuery()
            .userId(userId)
            .singleResult();
    }
    
    protected void createProcessUser(rs.ac.uns.ftn.graduation.model.User user) {
        User newUser = identityService.newUser(user.getId().toString());

        newUser.setEmail(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(user.getPassword());

        identityService.saveUser(newUser);
    }
}
