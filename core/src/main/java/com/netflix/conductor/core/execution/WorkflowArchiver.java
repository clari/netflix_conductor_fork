package com.netflix.conductor.core.execution;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.IndexDAO;

import javax.annotation.Nullable;

public interface WorkflowArchiver {

    static final String ARCHIVED_FIELD = "archived";
    static final String RAW_JSON_FIELD = "rawJSON";

    void archiveWorkflow(Workflow workflow, Configuration config, IndexDAO indexDAO, ExecutionDAO executionDAO, ObjectMapper objectMapper, @Nullable AmazonS3 s3Client);
}
