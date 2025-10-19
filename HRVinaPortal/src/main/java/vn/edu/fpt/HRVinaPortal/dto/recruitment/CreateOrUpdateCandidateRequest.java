// src/main/java/vn/edu/fpt/HRVinaPortal/dto/recruitment/CreateOrUpdateCandidateRequest.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CreateOrUpdateCandidateRequest {
    private String fullName;
    private Boolean gender;
    private Date dob;          // yyyy-MM-dd
    private String phone;
    private String email;
    private String address;
    private String resumeUrl;
    private String status;
    private String userAction;

    // ✅ THÊM: Danh sách JobPostingID mà ứng viên ứng tuyển
    private List<Integer> appliedJobPostingIds;
}
