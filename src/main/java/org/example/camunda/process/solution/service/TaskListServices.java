package org.example.camunda.process.solution.service;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.auth.SimpleAuthentication;
import io.camunda.tasklist.dto.Form;
import io.camunda.tasklist.dto.Pagination;
import io.camunda.tasklist.dto.TaskList;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.dto.Variable;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.camunda.process.solution.ash.dto.Task;
import org.example.camunda.process.solution.ash.dto.TaskSearch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskListServices {

  private CamundaTaskListClient client;

  @Autowired private ZeebeClient zeebeClient;

  private CamundaTaskListClient getCamundaTaskListClient() throws TaskListException {

    /*final ZeebeClient client =
            ZeebeClient.newClientBuilder().
    gatewayAddress("127.0.0.1:26500").build();*/

    /*client =
    new CamundaTaskListClient.Builder()
            .shouldReturnVariables()
            .taskListUrl("http://localhost:8082")
            .build();*/
    /*
    SaasAuthentication sa = new SaasAuthentication("zeebe", "zecret");
    client =
            new CamundaTaskListClient.Builder()
                    .shouldReturnVariables()
                    .taskListUrl("http://localhost:8082")
                    .authentication(sa)
                    .build();*/

    /* SelfManagedAuthentication sma = new SelfManagedAuthentication().clientId("java").
            clientSecret("foTPogjlI0hidwbDZcYFWzmU8FOQwLx0").
            baseUrl("http://localhost:18080").
            keycloakRealm("camunda-platform");
    CamundaTaskListClient client = new CamundaTaskListClient.Builder().shouldReturnVariables().taskListUrl("http://localhost:8082/").authentication(sma).build();*/

    /* final String ZEEBE_ADDRESS = "localhost:26500";
    final String ZEEBE_CLIENT_ID = "zeebe";
    final String ZEEBE_CLIENT_SECRET = "zecret";
    final String ZEEBE_CLIENT_AUDIENCE = "zeebe-api";
    final String ZEEBE_AUTHORIZATION_SERVER_URL = "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token";*/

    final String ZEEBE_ADDRESS = "localhost:8084";
    final String ZEEBE_CLIENT_ID = "tasklist";
    final String ZEEBE_CLIENT_SECRET = "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7";
    final String ZEEBE_CLIENT_AUDIENCE = "tasklist-api";
    final String ZEEBE_AUTHORIZATION_SERVER_URL =
        "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token";

    /*final OAuthCredentialsProvider provider =
            new OAuthCredentialsProviderBuilder()
                    .clientId(ZEEBE_CLIENT_ID)
                    .clientSecret(ZEEBE_CLIENT_SECRET)
                    .audience(ZEEBE_CLIENT_AUDIENCE)
                    .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
                    .build();

    final ZeebeClient client =
            new ZeebeClientBuilderImpl()
                    .gatewayAddress(ZEEBE_ADDRESS).usePlaintext()
                    .credentialsProvider(provider)
                    .build();*/

    /*  // Self Managed
    SelfManagedAuthentication sma =
        new SelfManagedAuthentication()
            .clientId(ZEEBE_CLIENT_ID)
            .clientSecret(ZEEBE_CLIENT_SECRET)
            .keycloakUrl("http://localhost:18080")
                .keycloakRealm("camunda-platform");
    client =
        new CamundaTaskListClient.Builder()
            .shouldReturnVariables()
            .taskListUrl("http://localhost:8082")
            .authentication(sma)
            .build();
    return client;*/

    // Simple Authentication
    SimpleAuthentication sa = new SimpleAuthentication("demo", "demo");
    client =
        new CamundaTaskListClient.Builder()
            .taskListUrl("http://localhost:8082")
            .shouldReturnVariables()
            .authentication(sa)
            .build();
    return client;
  }

  public Task claim(String taskId, String assignee) throws TaskListException {
    return convert(getCamundaTaskListClient().claim(taskId, assignee));
  }

  public Task unclaim(String taskId) throws TaskListException {
    return convert(getCamundaTaskListClient().unclaim(taskId));
  }

  public Task getTask(String taskId) throws TaskListException {
    return convert(getCamundaTaskListClient().getTask(taskId));
  }

  public List<Task> getTasks(TaskSearch taskSearch) throws TaskListException {
    Pagination pagination =
        new Pagination()
            .setPageSize(taskSearch.getPageSize())
            .setSearch(taskSearch.getSearch())
            .setSearchType(taskSearch.getDirection());
    if (Boolean.TRUE.equals(taskSearch.getAssigned()) && taskSearch.getAssignee() != null) {
      return convert(
          getCamundaTaskListClient()
              .getAssigneeTasks(
                  taskSearch.getAssignee(), TaskState.fromJson(taskSearch.getState()), pagination));
    }
    if (taskSearch.getGroup() != null) {
      return convert(
          getCamundaTaskListClient()
              .getGroupTasks(
                  taskSearch.getGroup(), TaskState.fromJson(taskSearch.getState()), pagination));
    }
    return convert(
        getCamundaTaskListClient()
            .getTasks(
                taskSearch.getAssigned(), TaskState.fromJson(taskSearch.getState()), pagination));
  }

  public List<Task> getGroupTasks(String group, TaskState state, Integer pageSize)
      throws TaskListException {
    return convert(getCamundaTaskListClient().getGroupTasks(group, state, pageSize));
  }

  public List<Task> getAssigneeTasks(String assignee, TaskState state, Integer pageSize)
      throws TaskListException {
    return convert(getCamundaTaskListClient().getAssigneeTasks(assignee, state, pageSize));
  }

  public List<Task> getTasks(TaskState state, Integer pageSize) throws TaskListException {
    return convert(getCamundaTaskListClient().getTasks(null, state, pageSize));
  }

  public void completeTask(String taskId, Map<String, Object> variables) throws TaskListException {

    getCamundaTaskListClient().completeTask(taskId, variables);
  }

  public void completeTaskWithJobKey(Long jobKey, Map<String, Object> variables) {
    zeebeClient.newCompleteCommand(jobKey).variables(variables).send();
  }

  public String getForm(String processDefinitionId, String formId) throws TaskListException {
    Form form = getCamundaTaskListClient().getForm(formId, processDefinitionId);
    return form.getSchema();
  }

  private Task convert(io.camunda.tasklist.dto.Task task) {
    Task result = new Task();
    BeanUtils.copyProperties(task, result);
    if (task.getVariables() != null) {
      result.setVariables(new HashMap<>());
      for (Variable var : task.getVariables()) {
        result.getVariables().put(var.getName(), var.getValue());
      }
    }
    return result;
  }

  private List<Task> convert(TaskList tasks) {
    List<Task> result = new ArrayList<>();
    for (io.camunda.tasklist.dto.Task task : tasks) {
      result.add(convert(task));
    }
    return result;
  }
}
