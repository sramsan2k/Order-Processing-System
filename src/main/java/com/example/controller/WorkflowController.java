package com.example.controller;

import com.example.response.ApiResponse;
import com.example.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<String>> triggerPendingToProcessingJob() {
        try {
            workflowService.processPendingOrders();
            return ResponseEntity.ok(ApiResponse.success("Workflow job executed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("Workflow execution failed: " + e.getMessage()));
        }
    }
}
