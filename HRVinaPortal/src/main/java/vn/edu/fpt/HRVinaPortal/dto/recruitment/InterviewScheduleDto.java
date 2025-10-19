// InterviewScheduleDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class InterviewScheduleDto {
    private Integer interviewId;
    private Date startTime;
    private Date endTime;
    private String location;
    private String status;
    private String notes;
    private Integer jobPostingId;
    private String jobName;
    private List<Integer> candidateIds;
}
