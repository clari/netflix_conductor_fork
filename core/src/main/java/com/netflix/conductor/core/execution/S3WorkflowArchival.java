package com.netflix.conductor.core.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.IndexDAO;
import org.apache.bval.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
public class S3WorkflowArchival implements WorkflowArchiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3WorkflowArchival.class);

    @Override
    public void archiveWorkflow(Workflow workflow, Configuration config, IndexDAO indexDAO, ExecutionDAO executionDAO, ObjectMapper objectMapper, AmazonS3 s3Client) {

        String bucketName = config.getS3ArchivalBucketURI();
        String fileName = workflow.getWorkflowName() + ".json";

        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(bucketName), "S3 bucket URI cannot be blank");
            // Upload workflow as a json file to s3
            s3Client.putObject(bucketName, fileName, objectMapper.writeValueAsString(workflow));
            LOGGER.info("Successfully archived workflow {} to S3 bucket {} as file {}", workflow.getWorkflowId(), bucketName, fileName);
        }  catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            String errorMsg = String.format("S3 server exception for workflow: %s", workflow.getWorkflowId());
            LOGGER.error(errorMsg, e);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            String errorMsg = String.format("S3 client exception for workflow: %s", workflow.getWorkflowId());
            LOGGER.error(errorMsg, e);
        } catch (Exception e) {
            // All other errors
            String errorMsg = String.format("Error archiving workflow: %s to S3", workflow.getWorkflowId());
            LOGGER.error(errorMsg, e);
        }

    }
}