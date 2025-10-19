package vn.edu.fpt.HRVinaPortal.exception.recruitment;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này được ném ra khi cố gắng tạo một tài nguyên đã tồn tại.
 * Mặc định sẽ trả về mã lỗi HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT) // <<< Rất quan trọng
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}

