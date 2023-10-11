package org.example.camunda.process.solution.auth;

import org.example.camunda.process.solution.TaskListRestClient;
import org.example.camunda.process.solution.exception.TaskListException;

public interface Authentication {

  public Boolean authenticate(TaskListRestClient taskListClient) throws TaskListException;
}
