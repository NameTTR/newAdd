package com.family.re.service.impl;

import com.family.re.service.IRePoolService;
import org.springframework.stereotype.Component;

@Component
public class ReRunJob {

    private final IRePoolService rePoolService;

    public ReRunJob(IRePoolService rePoolService) {
        this.rePoolService = rePoolService;
    }


    public void runJob() {
        rePoolService.addReachPool();
    }
}
