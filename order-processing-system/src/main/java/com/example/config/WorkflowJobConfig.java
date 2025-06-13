package com.example.config;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for scheduled workflow/background jobs.
 */
public class WorkflowJobConfig {

    // How often to run the pending-order processor (in minutes)
    public static final long WORKFLOW_JOB_FIXED_DELAY_MINUTES = 5;

    // Delay before first execution (optional)
    public static final long WORKFLOW_JOB_INITIAL_DELAY_MINUTES = 0;

    // Time unit used in scheduler
    public static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
}
