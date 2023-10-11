package org.example.camunda.process.solution.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExampleJobWorkersTask {

  private static Logger log = LoggerFactory.getLogger(ExampleJobWorkersTask.class);
  private final ZeebeClient zeebe;
  // private final DmnRepository repository;
  // private final DmnEngine dmnEngine;

  private static final String DECISION_ID_HEADER = "decisionRef";

  public ExampleJobWorkersTask(ZeebeClient zeebe) {
    this.zeebe = zeebe;
    // this.repository = repository;
    // this.dmnEngine = dmnEngine;
  }

  @JobWorker(
      type = "io.camunda.zeebe:userTask",
      autoComplete = false) // autoComplete = true as default value
  public void handleUserDetailsInputJob(final JobClient client, final ActivatedJob job) {
    log.info("inside handleUserDetailsInputJob--Start");

    System.out.println("job.getElementId()--->" + job.getElementId());
    logJob(job, null);
    log.info("inside handleUserDetailsInputJob --End");

    // CompleteuserDetailsInput(client, job);
  }

  private void CompleteuserDetailsInput(JobClient client, ActivatedJob job) {
    // Logic to complete the task after user's action, for instance:
    log.info("completeUserTask--start");

    System.out.println(job.getElementId());

    Map variables = job.getVariablesAsMap();

    // Post Script

    Map<String, Object> variables1 = new java.util.HashMap<>();
    variables1.put("newVariable", "VariableFromClient");
    client.newCompleteCommand(job.getKey()).variables(variables1).send().join();
    /*client
    .newCompleteCommand(job.getKey())
    .variables(Map.of("newVariable", "VariableFromClient"))
    .send()
    .join();*/
    log.info("completeUserTask--end");
    // return
    // Collections.singletonMap("customvalue1","{\"attachment\":null,\"communicationPreference\":null,\"otherOffices\":null,\"personalDetails\":null,\"primaryAddress\":null,\"variableDetails\":{\"businessKey\":\"US10547\",\"channelId\":\"CCR\",\"data\":{\"authentication\":{\"authenticated\":false},\"start\":{\"attachPrescriber\":null,\"isPrescirber\":true}},\"deviceInformation\":{\"clientIpAddress\":\"123.456.789.10\",\"deviceType\":\"windows-10\",\"operatingSystem\":\"PC\",\"region\":\"US\",\"userAgent\":\"Chrome/80.0.3987.149Safari/537.36\"},\"processInstanceId\":\"\",\"accessCode\":null,\"accessValidationFailed\":null,\"products\":[{\"productGroup\":\"IMiDS\",\"productName\":\"THALOMIDREMS\"},{\"productGroup\":\"IMiDS\",\"productName\":\"PomalystREMS\"},{\"productGroup\":\"IMiDS\",\"productName\":\"LenalidomideREMS\"}],\"status\":{\"code\":\"\",\"description\":\"\",\"nextStep\":null,\"previousStep\":null,\"taskId\":null,\"userAction\":null}}}");

  }

  private static void logJob(final ActivatedJob job, Object parameterValue) {
    log.info(
        "complete job\n>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variable parameter: {}\n[variables: {}]",
        job.getType(),
        job.getKey(),
        job.getElementId(),
        job.getProcessInstanceKey(),
        Instant.ofEpochMilli(job.getDeadline()),
        job.getCustomHeaders(),
        parameterValue,
        job.getVariables());
  }
}
