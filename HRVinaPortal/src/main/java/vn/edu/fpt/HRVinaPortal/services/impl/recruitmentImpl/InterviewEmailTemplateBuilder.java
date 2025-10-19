package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;


import vn.edu.fpt.HRVinaPortal.entities.recruitment.Candidate;
import vn.edu.fpt.HRVinaPortal.entities.recruitment.InterviewSchedule;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class InterviewEmailTemplateBuilder {

    private InterviewEmailTemplateBuilder() {}

    public static String defaultSubject(InterviewSchedule sched, Candidate cand) {
        return "[HR VINA] Thư mời phỏng vấn - " + safe(sched.getJobPosting().getJobName());
    }

    public static String defaultHtml(InterviewSchedule sched, Candidate cand, String companyName, String hotline) {
        // Định dạng thời gian local VN
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy");
        fmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Date start = sched.getStartTime();
        String timeStr = start == null ? "" : fmt.format(start);

        String jobName = safe(sched.getJobPosting().getJobName());
        String location = safe(sched.getLocation());
        String room = safe(sched.getRoom());
        String status = safe(sched.getStatus());
        String notes = safe(sched.getNotes());
        String candidateName = safe(cand.getFullName());
        String email = safe(cand.getEmail());

        String company = (companyName == null || companyName.isBlank()) ? "HR VINA" : companyName;
        String hl = (hotline == null || hotline.isBlank()) ? "1900 6789" : hotline;

        return """
               <div style="font-family:Arial,Helvetica,sans-serif;line-height:1.6;">
                 <p>Chào <b>%s</b>,</p>
                 <p>Chúng tôi là <b>%s</b>. Cảm ơn bạn đã quan tâm vị trí <b>%s</b>.</p>
                 <p>Chúng tôi trân trọng mời bạn tham gia buổi <b>phỏng vấn</b> theo thông tin:</p>
                 <ul>
                   <li><b>Thời gian:</b> %s</li>
                   <li><b>Địa điểm:</b> %s %s</li>
                   <li><b>Trạng thái lịch:</b> %s</li>
                   <li><b>Ghi chú:</b> %s</li>
                 </ul>
                 <p>Vui lòng phản hồi email này để <b>xác nhận tham dự</b> hoặc thông báo nếu bạn muốn điều chỉnh thời gian.</p>
                 <p>Mọi thắc mắc xin liên hệ: <b>%s</b>.</p>
                 <p>Trân trọng,<br/>Phòng Nhân sự - %s</p>
                 <hr/>
                 <p style="color:#666;font-size:12px">Gửi đến: %s</p>
               </div>
               """.formatted(candidateName, company, jobName, timeStr,
                location, (room == null || room.isBlank() ? "" : " - Phòng " + room),
                status, notes, hl, company, email);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

