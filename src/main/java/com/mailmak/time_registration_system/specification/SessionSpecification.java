package com.mailmak.time_registration_system.specification;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mailmak.time_registration_system.dto.sessions.GetSessionRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.mailmak.time_registration_system.classes.ProjectSession;
import com.mailmak.time_registration_system.classes.SessionState;

public class SessionSpecification {

    public static Specification<ProjectSession> hasCustomer(UUID customerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("task").get("project").get("customer").get("id"), customerId);
    }

    public static Specification<ProjectSession> hasProjectId(UUID projectId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("task").get("project").get("id"), projectId);
    }

    public static Specification<ProjectSession> hasTask(UUID taskId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("task").get("id"), taskId);
    }

    public static Specification<ProjectSession> hasUser(UUID userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ProjectSession> hasState(SessionState state) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<ProjectSession> startDate(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("period").get("startDate"), startDate);
    }

    public static Specification<ProjectSession> endDate(LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("period").get("endDate"), endDate);
    }

    public static Specification<ProjectSession> filter(GetSessionRequest request) {
        Specification<ProjectSession> specification = Specification.where(null);

        if (request.getCustomerId() != null) {
            specification = specification.and(hasCustomer(request.getCustomerId()));
        }

        if (request.getProjectId() != null) {
            specification = specification.and(hasProjectId(request.getProjectId()));
        }

        if (request.getTaskId() != null) {
            specification = specification.and(hasTask(request.getTaskId()));
        }

        if (request.getUserId() != null) {
            specification = specification.and(hasUser(request.getUserId()));
        }

        if (request.getState() != null) {
            specification = specification.and(hasState(request.getState()));
        }

        if (request.getStartDate() != null) {
            specification = specification.and(startDate(request.getStartDate()));
        }

        if (request.getEndDate() != null) {
            specification = specification.and(endDate(request.getEndDate()));
        }

        return specification;
    }

    public static Specification<ProjectSession> isActive(LocalDateTime startedBefore) {
        return (root, query, criteriaBuilder) -> {
            Predicate startDateCondition = criteriaBuilder.lessThanOrEqualTo(
                    root.get("period").get("startDate"),
                    startedBefore
            );

            Predicate endDateCondition = criteriaBuilder.isNull(
                    root.get("period").get("endDate")
            );

            // Combine both conditions with AND
            return criteriaBuilder.and(startDateCondition, endDateCondition);
        };
    }
}