package org.example.camunda.process.solution.ash.dto;

public class Employee {
  private String empName;
  private String location;

  public String getEmpName() {
    return empName;
  }

  public void setEmpName(String empName) {
    this.empName = empName;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getNextTask() {
    return nextTask;
  }

  public void setNextTask(String nextTask) {
    this.nextTask = nextTask;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  private String nextTask;
  private int age;
}
