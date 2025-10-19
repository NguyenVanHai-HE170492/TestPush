package vn.edu.fpt.HRVinaPortal.mappers.recruitment;

import org.springframework.stereotype.Component;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateInterviewRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.InterviewScheduleDto;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.InterviewSchedule;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.JobPosting;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InterviewScheduleMapper {

    private static Integer minutesBetween(Date start, Date end){
        if (start == null || end == null) return null;
        long diffMs = end.getTime() - start.getTime();
        return diffMs <= 0 ? 0 : (int) (diffMs / 60_000);
    }
    private static Date addMinutes(Date start, Integer minutes){
        if (start == null || minutes == null) return null;
        return new Date(start.getTime() + minutes.longValue() * 60_000L);
    }

    // Entity -> DTO
    public InterviewScheduleDto toDto(InterviewSchedule e) {
        if (e == null) return null;
        InterviewScheduleDto d = new InterviewScheduleDto();
        d.setInterviewId(e.getInterviewId());
        d.setStartTime(e.getStartTime());
        d.setEndTime(addMinutes(e.getStartTime(), e.getDuration()));
        d.setLocation(e.getLocation());
        d.setStatus(e.getStatus());
        d.setNotes(e.getNotes());
        if (e.getJobPosting() != null) {
            d.setJobPostingId(e.getJobPosting().getJobPostingId());
            d.setJobName(e.getJobPosting().getJobName());
        }
        if (e.getCandidates() != null) {
            d.setCandidateIds(
                    e.getCandidates().stream().map(Candidate::getCandidateId).collect(Collectors.toList())
            );
        }
        return d;
    }

    // CREATE
    public InterviewSchedule toEntityForCreate(CreateOrUpdateInterviewRequest r,
                                               JobPosting jobPosting,
                                               List<Candidate> candidates,
                                               String actor) {
        InterviewSchedule e = new InterviewSchedule();
        mapCommon(r, e);
        e.setJobPosting(jobPosting);
        if (candidates != null) e.setCandidates(candidates);
        e.setCreatedBy(actor);
        e.setCreatedAt(new Date());
        return e;
    }

    // UPDATE (in-place)
    public void updateEntityFromRequest(CreateOrUpdateInterviewRequest r,
                                        InterviewSchedule e,
                                        JobPosting newJobOrNull,
                                        List<Candidate> newCandidatesOrNull,
                                        String actor) {
        mapCommon(r, e);
        if (newJobOrNull != null) e.setJobPosting(newJobOrNull);
        if (newCandidatesOrNull != null) e.setCandidates(newCandidatesOrNull);
        e.setUpdatedBy(actor);
        e.setUpdatedAt(new Date());
    }

    private void mapCommon(CreateOrUpdateInterviewRequest r, InterviewSchedule e){
        if (r.getStartTime() != null) e.setStartTime(r.getStartTime());

        Integer duration = r.getDuration();
        if (duration == null && r.getStartTime() != null && r.getEndTime() != null) {
            duration = minutesBetween(r.getStartTime(), r.getEndTime());
        }
        if (duration != null) e.setDuration(duration);

        if (r.getLocation() != null) e.setLocation(r.getLocation());
        if (r.getRoom() != null) e.setRoom(r.getRoom());
        if (r.getStatus() != null) e.setStatus(r.getStatus());
        if (r.getNotes() != null) e.setNotes(r.getNotes());
    }
}
