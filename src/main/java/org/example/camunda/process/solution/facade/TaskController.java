package org.example.camunda.process.solution.facade;

import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.example.camunda.process.solution.ProcessApplication;
import org.example.camunda.process.solution.ProcessConstants;
import org.example.camunda.process.solution.ProcessVariables;
import org.example.camunda.process.solution.ash.dto.Task;
import org.example.camunda.process.solution.ash.dto.TaskSearch;
import org.example.camunda.process.solution.service.TaskListServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

  private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);
  private final ZeebeClient zeebe;
  @Autowired private ZeebeClient client;
  @Autowired private TaskListServices taskListService;

  private static Logger log = LoggerFactory.getLogger(ProcessApplication.class);

  public TaskController(ZeebeClient client) {
    this.zeebe = client;
  }

  @PostMapping("/start")
  public void startProcessInstance(@RequestBody ProcessVariables variables) {

    LOG.info(
        "Starting process `"
            + ProcessConstants.BPMN_PROCESS_ID_Ash
            + "` with variables: "
            + variables);

    final ProcessInstanceEvent event =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("ashDemoProcess")
            .latestVersion()
            .variables(
                "{\"a\": \""
                    + UUID.randomUUID().toString()
                    + "\",\"b\": \""
                    + new Date().toString()
                    + "\"}")
            .send()
            .join();

    log.info(
        "started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
        event.getProcessDefinitionKey(),
        event.getBpmnProcessId(),
        event.getVersion(),
        event.getProcessInstanceKey());
  }

  @PostMapping("/message/{messageName}/{correlationKey}")
  public void publishMessage(
      @PathVariable String messageName,
      @PathVariable String correlationKey,
      @RequestBody ProcessVariables variables) {

    LOG.info(
        "Publishing message `{}` with correlation key `{}` and variables: {}",
        messageName,
        correlationKey,
        variables);

    zeebe
        .newPublishMessageCommand()
        .messageName(messageName)
        .correlationKey(correlationKey)
        .variables(variables)
        .send();
  }

  @GetMapping(path = "/getTasks")
  public List<Task> getTasks() throws TaskListException {
    return taskListService.getTasks(null, null);
  }

  @PostMapping("/search")
  public List<Task> searchTasks(@RequestBody TaskSearch taskSearch) throws TaskListException {
    return taskListService.getTasks(taskSearch);
  }

  @GetMapping("/{taskId}/claim")
  public Task claimTask(@PathVariable String taskId) throws TaskListException {
    String username = "demo";
    return taskListService.claim(taskId, username);
  }

  @GetMapping("/{taskId}/unclaim")
  public Task unclaimTask(@PathVariable String taskId) throws TaskListException {
    return taskListService.unclaim(taskId);
  }

  @PostMapping("/{taskId}")
  public void completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> variables)
      throws TaskListException {

    LOG.info("Completing task " + taskId + "` with variables: " + variables);
    taskListService.completeTask(taskId, variables);
  }

  @PostMapping("/withJobKey/{jobKey}")
  public void completeTaskWithJobKey(
      @PathVariable Long jobKey, @RequestBody Map<String, Object> variables)
      throws TaskListException {

    LOG.info("Completing task by job key " + jobKey + "` with variables: " + variables);
    taskListService.completeTaskWithJobKey(jobKey, variables);
  }
}
