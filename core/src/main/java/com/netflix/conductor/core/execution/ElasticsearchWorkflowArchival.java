/*
 * Copyright 2021 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.core.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.dao.IndexDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ElasticsearchWorkflowArchival implements WorkflowArchiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchWorkflowArchival.class);

    private final IndexDAO indexDAO;
    private final ObjectMapper objectMapper;

    static final String ARCHIVED_FIELD = "archived";
    static final String RAW_JSON_FIELD = "rawJSON";

    @Inject
    public ElasticsearchWorkflowArchival(IndexDAO indexDAO, ObjectMapper objectMapper) {
        this.indexDAO = indexDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    public void archiveWorkflow(Workflow workflow) {
        try {
            indexDAO.updateWorkflow(workflow.getWorkflowId(),
                    new String[]{RAW_JSON_FIELD, ARCHIVED_FIELD},
                    new Object[]{objectMapper.writeValueAsString(workflow), true});
            LOGGER.info("Successfully archived workflow {} to Elasticsearch", workflow.getWorkflowId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
