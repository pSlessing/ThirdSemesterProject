package com.mailmak.time_registration_system.scheduledTasks;

import com.mailmak.time_registration_system.classes.ProjectSession;
import com.mailmak.time_registration_system.service.SessionServiceInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CronJobs {
    private final Logger logger = LoggerFactory.getLogger(CronJobs.class);

    private final SessionServiceInterface sessionService;

    public CronJobs(SessionServiceInterface sessionService) {
        this.sessionService = sessionService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void closeActiveSessionsOlderThanSpecifiedHours(){
        int specifiedHours = 8;
        LocalDateTime startedBefore = LocalDateTime.now().minusHours(specifiedHours);

        List<ProjectSession> activeSessions = sessionService.getActiveSessions(startedBefore);

        for (ProjectSession session : activeSessions) {
            try {
                session.complete();
                sessionService.saveSession(session);
            } catch (Exception e) {
                logger.error("Error while closing session: {}", e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * 1-5")
    public void extractCheckinSessionsFromKeyCardSystem() {
        logger.info("Extracting checkin sessions from key card system...");
    }
}
