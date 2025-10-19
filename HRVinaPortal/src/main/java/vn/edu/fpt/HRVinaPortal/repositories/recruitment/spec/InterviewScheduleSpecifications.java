package vn.edu.fpt.HRVinaPortal.repositories.recruitment.spec;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.InterviewSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InterviewScheduleSpecifications {

    public static Specification<InterviewSchedule> filter(
            Date from, Date to,
            String status, String locationOrNoteLike,
            Integer jobPostingId, Integer candidateId, Integer companyId
    ) {
        return (root, query, cb) -> {
            // ⚠️ Không fetch to-many khi phân trang
            List<Predicate> predicates = new ArrayList<>();

            if (from != null && to != null) {
                predicates.add(cb.between(root.get("startTime"), from, to));
            } else if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), from));
            } else if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), to));
            }

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(locationOrNoteLike)) {
                String like = "%" + locationOrNoteLike.trim().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("location")), like),
                                cb.like(cb.lower(root.get("notes")), like)
                        )
                );
            }

            if (jobPostingId != null) {
                predicates.add(cb.equal(root.get("jobPosting").get("jobPostingId"), jobPostingId));
            }

            if (companyId != null) {
                predicates.add(cb.equal(root.get("jobPosting").get("company").get("companyId"), companyId));
            }

            if (candidateId != null) {
                Join<Object, Object> candJoin = root.join("candidates", JoinType.INNER);
                predicates.add(cb.equal(candJoin.get("candidateId"), candidateId));
            }

            // tránh trùng bản ghi khi có join
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
