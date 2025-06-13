package com.example.controller;

import com.example.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowService workflowService;

    @Test
    void testTriggerWorkflowSuccess() throws Exception {
        doNothing().when(workflowService).processPendingOrders();

        mockMvc.perform(post("/api/workflow/trigger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workflow job executed successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testTriggerWorkflowFailure() throws Exception {
        doThrow(new RuntimeException("Kafka unavailable")).when(workflowService).processPendingOrders();

        mockMvc.perform(post("/api/workflow/trigger"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Workflow execution failed: Kafka unavailable"));
    }
}
