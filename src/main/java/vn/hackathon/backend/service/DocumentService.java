package vn.hackathon.backend.service;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import vn.hackathon.backend.dto.DocumentDto;

public interface DocumentService {
  DocumentDto uploadDocument(MultipartFile file, UUID classId);
}
