package com.netflix.conductor.core.execution;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.IndexDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchWorkflowArchival implements WorkflowArchiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchWorkflowArchival.class);

    @Override
    public void archiveWorkflow(Workflow workflow, Configuration config, IndexDAO indexDAO, ExecutionDAO executionDAO, ObjectMapper objectMapper, AmazonS3 s3Client) {
        try {
            indexDAO.updateWorkflow(workflow.getWorkflowId(),
                    new String[]{RAW_JSON_FIELD, ARCHIVED_FIELD},
                    new Object[]{objectMapper.writeValueAsString(workflow), true});
            LOGGER.info("Successfully archived workflow {} to Elasticsearch", workflow.getWorkflowId());
        } catch (Exception e) {
            String errorMsg = String.format("Error archiving workflow: %s to Elasticsearch", workflow.getWorkflowId());
            LOGGER.error(errorMsg, e);
        }
    }
}
