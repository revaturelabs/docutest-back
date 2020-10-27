package com.revature.templates;

import com.revature.models.SwaggerSummary;
import lombok.Data;

@Data
public class SwaggerSummaryDTO {
    private int id;
    private String testPlanName;
    private int loops;
    private int duration;
    private int threads;
    private int rampUp;
    private boolean followRedirects;
    
    public SwaggerSummaryDTO(SwaggerSummary s) {
        this.id = s.getId();
        this.testPlanName = s.getTestPlanName();
        this.loops = s.getLoops();
        this.duration = s.getDuration();
        this.threads = s.getThreads();
        this.rampUp = s.getRampUp();
        this.followRedirects = s.isFollowRedirects();
    }
}
