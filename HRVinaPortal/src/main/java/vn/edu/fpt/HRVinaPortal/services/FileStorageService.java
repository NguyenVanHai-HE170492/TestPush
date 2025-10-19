package vn.edu.fpt.HRVinaPortal.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is empty");
        // Cloudinary trả raw Map => ép kiểu an toàn bằng unchecked cast
        return (Map<String, Object>) cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder, "resource_type", "auto", "overwrite", true)
        );
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> delete(String publicId) throws IOException {
        return (Map<String, Object>) cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("invalidate", true, "resource_type", "raw")
        );
    }
}

