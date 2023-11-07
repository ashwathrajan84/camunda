package org.example.camunda.process.solution.dto;

public class TaskAssignRequest {

  String assignee;
  Boolean allowOverrideAssignment;

  public TaskAssignRequest() {}

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public Boolean getAllowOverrideAssignment() {
    return allowOverrideAssignment;
  }

  public void setAllowOverrideAssignment(Boolean allowOverrideAssignment) {
    this.allowOverrideAssignment = allowOverrideAssignment;
  }
}