package org.example.camunda.process.solution;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.util.List;
import org.example.camunda.process.solution.auth.JWTAuthentication;
import org.example.camunda.process.solution.dto.AccessTokenRequest;
import org.example.camunda.process.solution.dto.Constants;
import org.example.camunda.process.solution.dto.TaskSearchRequest;
import org.example.camunda.process.solution.dto.TaskSearchResponse;
import org.example.camunda.process.solution.exception.TaskListException;
import org.example.camunda.process.solution.exception.TaskListRestException;
import org.example.camunda.process.solution.facade.ProcessController;
import org.example.camunda.process.solution.service.MyService;
import org.example.camunda.process.solution.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @see
 *     https://docs.camunda.io/docs/components/best-practices/development/testing-process-definitions/#writing-process-tests-in-java
 */
@SpringBootTest(classes = ProcessApplication.class) // will deploy BPMN & DMN models
@ZeebeSpringTest
public class TaskUnitTest {

  @Autowired private ProcessController processController;

  @Autowired private ZeebeTestEngine engine;

  @MockBean private MyService myService;

  @Autowired JWTAuthentication jwtAuthentication;
  @Autowired TaskListRestClient taskListRestClient;
  @Autowired ZeebeClient zeebeClient;

  @Value("${tasklist.unit-test.bpmnProcessId: 'tasklistRestAPIUnitTestProcess'}")
  String bpmnProcessId;

  @Value("${tasklist.unit-test.processDefinitionKey}")
  String processDefinitionKey;

  @Test
  public void jsonTest() throws JsonProcessingException {
    JsonUtils<AccessTokenRequest> jsonUtils = new JsonUtils<>(AccessTokenRequest.class);
    AccessTokenRequest accessTokenRequest =
        new AccessTokenRequest("xxx", "xxx", "tasklist.camunda.io", "client_credentials");
    String json = jsonUtils.toJson(accessTokenRequest);
    assertNotNull(json);
    assertEquals(
        "{\"client_id\":\"xxx\",\"client_secret\":\"xxx\",\"audience\":\"tasklist.camunda.io\",\"grant_type\":\"client_credentials\"}",
        json);

    AccessTokenRequest result = jsonUtils.fromJson(json);
    assertNotNull(result);
    assertEquals("xxx", result.getClient_id());
  }

  @Test
  public void zeebeStatus() {
    Topology topology = zeebeClient.newTopologyRequest().send().join();
    assertTrue(topology.getClusterSize() > 0);
  }

  @Test
  public void testHappyPath() throws Exception {
    // define mock behavior
    when(myService.myOperation(anyString())).thenReturn(true);

    // prepare data
    final ProcessVariables variables = new ProcessVariables().setBusinessKey("23");
    assertTrue(jwtAuthentication.authenticate(taskListRestClient));
    /*
    // start a process instance
    processController.startProcessInstance(variables);

    // wait for process to be started
    engine.waitForIdleState(Duration.ofSeconds(1));
    InspectedProcessInstance processInstance =
        InspectionUtility.findProcessInstances().findLastProcessInstance().get();
    assertThat(processInstance).isStarted();

    // check that service task has been completed
    waitForProcessInstanceHasPassedElement(processInstance, "Task_InvokeService");
    Mockito.verify(myService).myOperation("23");

    // check that process is ended with the right result
    waitForProcessInstanceCompleted(processInstance);
    assertThat(processInstance)
        .isCompleted()
        .hasPassedElement("Task_InvokeService")
        .hasVariableWithValue("result", true);

    // ensure no other side effects
    Mockito.verifyNoMoreInteractions(myService);
    */
  }

  public List<TaskSearchResponse> findCreatedUnAssignedTasks()
      throws TaskListException, TaskListRestException {
    TaskSearchRequest taskSearchRequest = new TaskSearchRequest();
    taskSearchRequest.setState(Constants.TASK_STATE_CREATED);
    taskSearchRequest.setAssigned(false);
    taskSearchRequest.setProcessDefinitionKey(processDefinitionKey);
    return taskListRestClient.searchTasks(taskSearchRequest);
  }
}
