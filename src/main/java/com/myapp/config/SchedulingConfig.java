package com.myapp.config;

import com.myapp.service.AuctionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final AuctionService auctionService;

    public SchedulingConfig(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkExpiredAuctions() {
        auctionService.checkAndUpdateExpiredAuctions();
    }
} 