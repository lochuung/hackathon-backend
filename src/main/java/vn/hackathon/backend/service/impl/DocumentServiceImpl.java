package vn.hackathon.backend.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.hackathon.backend.dto.DocumentDto;
import vn.hackathon.backend.entity.ClassEntity;
import vn.hackathon.backend.entity.Document;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.exception.BadRequestException;
import vn.hackathon.backend.exception.NotFoundException;
import vn.hackathon.backend.mapper.DocumentMapper;
import vn.hackathon.backend.repository.ClassRepository;
import vn.hackathon.backend.repository.DocumentRepository;
import vn.hackathon.backend.repository.UserRepository;
import vn.hackathon.backend.service.DocumentService;
import vn.hackathon.backend.service.S3Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final ClassRepository classRepository;
  private final UserRepository userRepository;
  private final S3Service s3Service;
  private final JwtService jwtService;
  private final DocumentMapper documentMapper;

  @Transactional
  public DocumentDto uploadDocument(MultipartFile file, UUID classId) {
    // Validate class exists
    ClassEntity classEntity =
        classRepository
            .findById(classId)
            .orElseThrow(() -> new NotFoundException("Class not found with id: " + classId));

    UUID uploadedByUserId = classEntity.getCreatedBy().getId();
    SecurityContext securityContext = SecurityContextHolder.getContext();
    UUID currentUserId =
        jwtService.getUserId((JwtAuthenticationToken) securityContext.getAuthentication());

    if (!uploadedByUserId.equals(currentUserId)) {
      throw BadRequestException.message(
          "You do not have permission to upload documents to this class");
    }

    // Validate user exists
    User user =
        userRepository
            .findById(uploadedByUserId)
            .orElseThrow(
                () -> new NotFoundException("User not found with id: " + uploadedByUserId));

    // Upload file to S3
    String folder = "documents/class-" + classId;
    String filePath = s3Service.uploadFile(file, folder);
    String fileUrl = s3Service.getFileUrl(filePath);

    // Get file type from original filename
    String fileType = getFileType(file.getOriginalFilename());

    // Create document entity
    Document document =
        Document.builder()
            .classEntity(classEntity)
            .uploadedBy(user)
            .fileName(file.getOriginalFilename())
            .filePath(filePath)
            .fileType(fileType)
            .fileUrl(fileUrl)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Document savedDocument = documentRepository.save(document);
    log.info("Document saved successfully: {}", savedDocument.getId());

    return documentMapper.toDto(savedDocument);
  }

  private String getFileType(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "unknown";
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
  }
}
