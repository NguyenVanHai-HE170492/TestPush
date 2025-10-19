// CandidateDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CandidateDto {
    private Integer candidateId;
    private String fullName;
    private Boolean gender;
    private Date dob;
    private String phone;
    private String email;
    private String address;
    private String resumeUrl;
    private String status;
    // ✅ THÊM MỚI: Danh sách các công việc đã ứng tuyển
    private List<AppliedJobInfo> appliedJobs;

    // ✅ Lớp nội tại để chứa thông tin rút gọn của công việc
    @Data
    public static class AppliedJobInfo {
        private Integer jobPostingId;
        private String jobName;
        private String companyName;
        private String jobTitleName;
    }
}
