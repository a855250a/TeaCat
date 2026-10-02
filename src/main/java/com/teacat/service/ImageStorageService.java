package com.teacat.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${cloudinary.cloud-name:}") private String cloudName;
    @Value("${cloudinary.api-key:}") private String apiKey;
    @Value("${cloudinary.api-secret:}") private String apiSecret;

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("檔案不可為空");
        if (!ALLOWED.contains(file.getContentType())) throw new IllegalArgumentException("僅支援 JPG、PNG、WEBP 圖片");

        if (cloudConfigured()) {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true));
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "teacat/pets",
                    "resource_type", "image"));
            Object url = result.get("secure_url");
            if (url == null) throw new IOException("Cloudinary 未回傳圖片網址");
            return url.toString();
        }

        // Local development fallback. Production should configure Cloudinary.
        File uploadDir = new File("uploads");
        if (!uploadDir.exists() && !uploadDir.mkdirs()) throw new IOException("無法建立上傳目錄");
        String ext = switch (file.getContentType()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String name = UUID.randomUUID() + ext;
        file.transferTo(new File(uploadDir, name).getAbsoluteFile());
        return "uploads/" + name;
    }

    public boolean cloudConfigured() {
        return notBlank(cloudName) && notBlank(apiKey) && notBlank(apiSecret);
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }
}
