/*
 * Copyright 2020 Netflix, Inc.
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

import com.netflix.conductor.common.run.Workflow;

public interface WorkflowArchiver {

    /**
     * method to archive workflow to Elasticsearch or S3
     * @param workflow The workflow to be archived
     */
    void archiveWorkflow(Workflow workflow);

    /**
     * Retrieves a specific field from the index
     * @param workflowInstanceId id of the workflow
     * @param key field to be retrieved
     * @return value of the field as string
     */
    String getWorkflow(String workflowInstanceId, String key);
}