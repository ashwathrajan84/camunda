package org.example.camunda.process.solution;

import java.util.List;
import java.util.Map;
import org.example.camunda.process.solution.dto.*;
import org.example.camunda.process.solution.exception.TaskListException;
import org.example.camunda.process.solution.exception.TaskListRestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public interface TaskListRestApi {

  List<TaskSearchResponse> searchTasks(TaskSearchRequest request)
      throws TaskListException, TaskListRestException;


  TaskResponse getTask( String taskId) throws TaskListException, TaskListRestException;

  FormResponse getForm(String processDefinitionKey, String formId)
      throws TaskListException, TaskListRestException;

  TaskResponse assignTask(String taskId, TaskAssignRequest request)
      throws TaskListException, TaskListRestException;

  TaskResponse unassignTask(String taskId) throws TaskListException, TaskListRestException;

  TaskResponse completeTask(String taskId, Map<String, Object> variablesMap)
      throws TaskListException, TaskListRestException;
}
