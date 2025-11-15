package vn.hackathon.backend.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hackathon.backend.dto.ApiResponse;
import vn.hackathon.backend.dto.DocumentDto;
import vn.hackathon.backend.service.DocumentService;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

  private final DocumentService documentService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<DocumentDto> uploadDocument(
      @RequestParam("file") MultipartFile file, @RequestParam(value = "classId") UUID classId) {

    log.info(
        "Upload document request - fileName: {}, size: {} bytes, classId: {}",
        file.getOriginalFilename(),
        file.getSize(),
        classId);

    DocumentDto documentDto = documentService.uploadDocument(file, classId);

    return ApiResponse.<DocumentDto>builder()
        .status("success")
        .message("Document uploaded successfully")
        .code(HttpStatus.OK.value())
        .data(documentDto)
        .build();
  }
}
