package org.example.camunda.process.solution.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(Include.NON_NULL)
public class ProcessVariable {

  private String businessKey;
  private Boolean result;

  public String getBusinessKey() {
    return businessKey;
  }

  public ProcessVariable setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
    return this;
  }

  public Boolean getResult() {
    return result;
  }

  public ProcessVariable setResult(Boolean result) {
    this.result = result;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(
        this,
        new MultilineRecursiveToStringStyle() {
          public ToStringStyle withShortPrefixes() {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            return this;
          }
        }.withShortPrefixes());
  }
}
