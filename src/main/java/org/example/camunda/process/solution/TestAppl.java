package org.example.camunda.process.solution;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

import java.net.http.HttpResponse;
import java.util.Date;
import java.util.UUID;
import org.example.camunda.process.solution.auth.Authentication;
import org.example.camunda.process.solution.auth.JWTAuthentication;
import org.example.camunda.process.solution.dto.TaskResponse;
import org.example.camunda.process.solution.exception.TaskListException;
import org.example.camunda.process.solution.exception.TaskListRestException;
import org.example.camunda.process.solution.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestAppl {
  private static final Logger LOG = LoggerFactory.getLogger(TestAppl.class);

  @Autowired JWTAuthentication jwtAuthentication;
 // TaskListRestApi taskListInterface = null;
  @Autowired TaskListRestClient taskListRestClient;
  @Autowired ZeebeClient zeebeClient;


  @PostMapping("/start")
  public void startProcessInstance(@RequestBody ProcessVariables variables) {
    TaskListRestClient restClient;
    JWTAuthentication jwtAuthentication;

    TaskListRestClient taskListRestClient;
    System.out.println("Instance Before");

    ProcessInstanceEvent event =
        zeebeClient
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

    Long processInstanceKey = event.getProcessInstanceKey();
    System.out.println("Instance Key : " + processInstanceKey);
  }

  @GetMapping(path = "/authenticate")
  public TaskListRestClient authenticationTest()
      throws org.example.camunda.process.solution.exception.TaskListException {
    Authentication authentication = taskListRestClient.getAuthentication();
    authentication.authenticate(taskListRestClient);
    return taskListRestClient; // jwtAuthentication.authenticate(taskListRestClient);
  }
    @GetMapping(path = "/{taskId}")
    public TaskResponse getTaskInfo(@PathVariable String taskId)
            throws TaskListException, TaskListRestException, JsonProcessingException {
        return taskListRestClient.getTask(taskId);

    }

}
