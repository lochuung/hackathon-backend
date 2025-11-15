package vn.hackathon.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import vn.hackathon.backend.domain.ClassDomainService;
import vn.hackathon.backend.dto.quiz.QuizCreateRequest;
import vn.hackathon.backend.dto.quiz.QuizDto;
import vn.hackathon.backend.entity.ClassEntity;
import vn.hackathon.backend.entity.Quiz;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.exception.BadRequestException;
import vn.hackathon.backend.exception.NotFoundException;
import vn.hackathon.backend.exception.UnauthorizedException;
import vn.hackathon.backend.mapper.QuizMapper;
import vn.hackathon.backend.repository.ClassRepository;
import vn.hackathon.backend.repository.QuizRepository;
import vn.hackathon.backend.repository.UserRepository;
import vn.hackathon.backend.service.AIService;
import vn.hackathon.backend.service.QuizService;
import vn.hackathon.backend.utils.QuestionValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final ClassRepository classRepository;
  private final UserRepository userRepository;
  private final QuizMapper quizMapper;
  private final ClassDomainService classDomainService;
  private final JwtService jwtService;
  private final AIService aIService;

  @Override
  @Transactional
  public QuizDto createQuiz(QuizCreateRequest quizDto, UUID classId, UUID userId) {
    log.debug("Creating quiz for class: {} by user: {}", classId, userId);

    // Validate user exists and get the user
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new NotFoundException(String.format("User with ID %s not found", userId)));

    // Validate class exists
    ClassEntity classEntity =
        classRepository
            .findById(classId)
            .orElseThrow(
                () -> new NotFoundException(String.format("Class with ID %s not found", classId)));

    // Validate required fields
    if (quizDto.getTitle() == null || quizDto.getTitle().trim().isEmpty()) {
      throw BadRequestException.message("Quiz title is required");
    }

    // Validate skills
    QuestionValidator.validateSkills(quizDto.getSkills());

    // Create quiz entity first to get the ID
    Quiz quiz =
        Quiz.builder()
            .title(quizDto.getTitle())
            .skills(quizDto.getSkills())
            .createdBy(user)
            .durationMinutes(quizDto.getDurationMinutes())
            .totalPoints(quizDto.getTotalPoints() != null ? quizDto.getTotalPoints() : 100.0)
            .isPublished(false)
            .classEntity(classEntity)
            .documentUrl(quizDto.getDocumentUrl())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Quiz savedQuiz = quizRepository.save(quiz);
    log.info("Quiz created successfully with ID: {}", savedQuiz.getId());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            aIService.callAI(savedQuiz);
          }
        });

    return quizMapper.toDto(savedQuiz);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizDto> getQuizzesByClass(UUID classId) {
    log.debug("Fetching quizzes for class: {}", classId);

    // Validate class exists
    classRepository
        .findById(classId)
        .orElseThrow(
            () -> new NotFoundException(String.format("Class with ID %s not found", classId)));

    List<Quiz> quizzes = quizRepository.findByClassEntity_Id(classId);

    return quizzes.stream().map(quizMapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public QuizDto getQuizById(UUID quizId) {
    log.debug("Fetching quiz with ID: {}", quizId);

    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(
                () -> new NotFoundException(String.format("Quiz with ID %s not found", quizId)));

    UUID currentUserId = jwtService.getUserId();

    if (!classDomainService.isParticipantInClass(quiz.getClassEntity().getId(), currentUserId)) {
      throw BadRequestException.message("You are not authorized to access this) quiz");
    }

    return quizMapper.toDto(quiz);
  }

  @Override
  @Transactional
  public QuizDto updateQuiz(UUID quizId, QuizDto quizDto, UUID userId) {
    log.debug("Updating quiz: {} by user: {}", quizId, userId);

    // Get the quiz
    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(
                () -> new NotFoundException(String.format("Quiz with ID %s not found", quizId)));

    // Check authorization - only creator can update
    if (!quiz.getCreatedBy().getId().equals(userId)) {
      throw new UnauthorizedException("Only the quiz creator can update this quiz");
    }

    // Validate required fields
    if (quizDto.getTitle() != null && quizDto.getTitle().trim().isEmpty()) {
      throw BadRequestException.message("Quiz title cannot be empty");
    }

    // Validate questions structure if provided
    if (quizDto.getQuestions() != null) {
      QuestionValidator.validateQuestions(quizDto.getQuestions());
    }

    // Validate skills if provided
    if (quizDto.getSkills() != null) {
      QuestionValidator.validateSkills(quizDto.getSkills());
    }

    // Update fields
    if (quizDto.getTitle() != null) {
      quiz.setTitle(quizDto.getTitle());
    }

    if (quizDto.getSkills() != null) {
      quiz.setSkills(quizDto.getSkills());
    }

    if (quizDto.getDurationMinutes() != null) {
      quiz.setDurationMinutes(quizDto.getDurationMinutes());
    }

    if (quizDto.getTotalPoints() != null) {
      quiz.setTotalPoints(quizDto.getTotalPoints());
    }

    if (quizDto.getQuestions() != null) {
      quiz.setQuestions(quizDto.getQuestions());
    }

    if (quizDto.getStats() != null) {
      quiz.setStats(quizDto.getStats());
    }

    if (quizDto.getIsPublished() != null) {
      quiz.setIsPublished(quizDto.getIsPublished());
    }

    if (quizDto.getDocumentUrl() != null) {
      quiz.setDocumentUrl(quizDto.getDocumentUrl());
    }

    quiz.setUpdatedAt(LocalDateTime.now());

    Quiz updatedQuiz = quizRepository.save(quiz);
    log.info("Quiz updated successfully with ID: {}", updatedQuiz.getId());

    return quizMapper.toDto(updatedQuiz);
  }
}
