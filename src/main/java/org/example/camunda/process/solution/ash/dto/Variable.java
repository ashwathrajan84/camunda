package org.example.camunda.process.solution.ash.dto;

import io.camunda.tasklist.dto.VariableType;

public class Variable {
  private String id;

  private String name;

  private Object value;

  private io.camunda.tasklist.dto.VariableType type;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public io.camunda.tasklist.dto.VariableType getType() {
    return type;
  }

  public void setType(VariableType type) {
    this.type = type;
  }
}
