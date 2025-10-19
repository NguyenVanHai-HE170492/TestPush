// CreateOrUpdateInterviewRequest.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CreateOrUpdateInterviewRequest {
    private Date startTime;         // yêu cầu khi create; optional khi update
    private Date endTime;           // optional nếu có duration
    private Integer duration;       // optional nếu có endTime
    private String location;
    private String room;            // NEW: để khỏi dùng reflection
    private String status;
    private String notes;
    private Integer jobPostingId;
    private List<Integer> candidateIds; // có thể rỗng
    private String userAction;
}
