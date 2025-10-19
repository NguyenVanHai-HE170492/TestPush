package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.Data;
import java.util.List;

@Data
public class SendEmailsRequest {
    private List<EmailPreviewDto> emails; // cho phép FE sửa tiêu đề/nội dung trước khi gửi
}
