package vn.hackathon.backend.service.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hackathon.backend.domain.ClassDomainService;
import vn.hackathon.backend.dto.quiz.QuizAttemptDto;
import vn.hackathon.backend.dto.quiz.QuizAttemptStartResponse;
import vn.hackathon.backend.dto.quiz.QuizAttemptSubmitResponse;
import vn.hackathon.backend.dto.quiz.QuizPipelineResponse;
import vn.hackathon.backend.entity.Quiz;
import vn.hackathon.backend.entity.QuizAttempt;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.exception.BadRequestException;
import vn.hackathon.backend.exception.NotFoundException;
import vn.hackathon.backend.repository.QuizAttemptRepository;
import vn.hackathon.backend.repository.QuizRepository;
import vn.hackathon.backend.repository.UserRepository;
import vn.hackathon.backend.service.QuizAttemptService;
import vn.hackathon.backend.service.RedisService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptServiceImpl implements QuizAttemptService {

  private final QuizAttemptRepository quizAttemptRepository;
  private final QuizRepository quizRepository;
  private final UserRepository userRepository;
  private final RedisService redisService;
  private final ClassDomainService classDomainService;

  @Override
  @Transactional
  public QuizAttemptStartResponse startQuizAttempt(UUID quizId, UUID userId) {
    log.info("Starting quiz attempt for quizId={}, userId={}", quizId, userId);

    // Validate quiz exists
    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + quizId));

    if (!classDomainService.isParticipantInClass(quiz.getClassEntity().getId(), userId)) {
      throw BadRequestException.message("User is not enrolled in the domain of this quiz");
    }

    // Validate user exists
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    // Check if quiz is published
    if (!Boolean.TRUE.equals(quiz.getIsPublished())) {
      throw BadRequestException.message("Quiz is not published yet");
    }

    // find existing active attempt
    List<QuizAttempt> existingAttempts =
        quizAttemptRepository.findActiveAttemptsByQuizAndUser(quizId, userId).stream()
            .filter(
                attempt ->
                    attempt.getEndTime() == null
                        && attempt.getExpiresAt().toInstant().isAfter(Instant.now()))
            .toList();
    if (!existingAttempts.isEmpty()) {
      log.info("Found existing active attempt for quizId={}, userId={}", quizId, userId);
      return buildStartResponse(existingAttempts.get(0), quiz);
    }

    // Create new quiz attempt
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(quiz.getDurationMinutes() * 60L);

    QuizAttempt attempt =
        QuizAttempt.builder()
            .quiz(quiz)
            .user(user)
            .startTime(Timestamp.from(now))
            .expiresAt(Timestamp.from(expiresAt))
            .answers(new HashMap<>())
            .skillsPoint(new HashMap<>())
            .build();

    attempt = quizAttemptRepository.save(attempt);
    log.info("Created quiz attempt with id={}", attempt.getId());

    // Build response with questions
    return buildStartResponse(attempt, quiz);
  }

  @Override
  @Transactional
  public void saveAnswers(UUID attemptId, Map<String, Integer> answers) {
    log.info("Auto-saving answers for attemptId={}", attemptId);

    // Validate attempt exists and is still active
    QuizAttempt attempt =
        quizAttemptRepository
            .findById(attemptId)
            .orElseThrow(
                () -> new NotFoundException("Quiz attempt not found with id: " + attemptId));

    if (attempt.getEndTime() != null) {
      throw BadRequestException.message("Quiz attempt has already been submitted");
    }

    if (attempt.getExpiresAt().toInstant().isBefore(Instant.now())) {
      attempt.setEndTime(attempt.getExpiresAt());
      quizAttemptRepository.save(attempt);
      throw BadRequestException.message("Quiz attempt has expired");
    }

    // Save to Redis with TTL equal to remaining time
    long remainingMinutes =
        Duration.between(Instant.now(), attempt.getExpiresAt().toInstant()).toMinutes() + 1;
    redisService.saveAttemptAnswers(attemptId, answers, remainingMinutes);

    log.info("Saved {} answers to Redis for attemptId={}", answers.size(), attemptId);
  }

  @Override
  @Transactional
  public QuizAttemptSubmitResponse submitQuizAttempt(UUID attemptId, Map<String, Integer> answers) {
    log.info("Submitting quiz attempt for attemptId={}", attemptId);

    // Validate attempt exists
    QuizAttempt attempt =
        quizAttemptRepository
            .findById(attemptId)
            .orElseThrow(
                () -> new NotFoundException("Quiz attempt not found with id: " + attemptId));

    if (attempt.getEndTime() != null) {
      throw BadRequestException.message("Quiz attempt has already been submitted");
    }

    Quiz quiz = attempt.getQuiz();

    // Merge answers from Redis if not in submission
    Map<String, Integer> redisAnswers = redisService.getAttemptAnswers(attemptId);
    if (redisAnswers != null && !redisAnswers.isEmpty()) {
      log.info("Found {} answers in Redis for attemptId={}", redisAnswers.size(), attemptId);
      for (Map.Entry<String, Integer> entry : redisAnswers.entrySet()) {
        answers.putIfAbsent(entry.getKey(), entry.getValue());
      }
    }

    // Calculate score and skills
    ScoreResult scoreResult = calculateScore(quiz, answers);

    // Update attempt
    Instant endTime = Instant.now();
    attempt.setEndTime(Timestamp.from(endTime));
    attempt.setScore(scoreResult.score);

    // Convert skillsPoint Map<String, Double> to Map<String, Object>
    Map<String, Object> skillsPointAsObject = new HashMap<>(scoreResult.skillsPoint);
    attempt.setSkillsPoint(skillsPointAsObject);

    // Convert answers Map<String, Integer> to Map<String, Object>
    Map<String, Object> answersAsObject = new HashMap<>(answers);
    attempt.setAnswers(answersAsObject);

    quizAttemptRepository.save(attempt);

    // Delete from Redis
    redisService.deleteAttemptAnswers(attemptId);

    log.info(
        "Quiz attempt submitted: attemptId={}, score={}, correctAnswers={}",
        attemptId,
        scoreResult.score,
        scoreResult.correctAnswers);

    // Calculate time taken
    long secondsTaken = Duration.between(attempt.getStartTime().toInstant(), endTime).getSeconds();
    String timeTaken = formatDuration(secondsTaken);

    return QuizAttemptSubmitResponse.builder()
        .attemptId(attempt.getId())
        .score(scoreResult.score)
        .totalPoints(quiz.getTotalPoints())
        .correctAnswers(scoreResult.correctAnswers)
        .totalQuestions(scoreResult.totalQuestions)
        .skillsPoint(scoreResult.skillsPoint)
        .endTime(endTime)
        .timeTaken(timeTaken)
        .build();
  }

  @Override
  public List<QuizPipelineResponse> getQuizResultsPipeline() {
    Instant now = Instant.now();
    // yesterday's date at 00:00
    // today's date at 00:00
    Instant startOfToday = now.truncatedTo(ChronoUnit.DAYS);
    Instant startOfYesterday = startOfToday.minus(1, ChronoUnit.DAYS);
    // find all quiz attempts from startOfYesterday to startOfToday
    List<QuizAttempt> attempts =
        quizAttemptRepository.findAllByEndTimeBetween(
            Timestamp.from(startOfYesterday), Timestamp.from(startOfToday));

    log.info(
        "Found {} quiz attempts between {} and {}",
        attempts.size(),
        startOfYesterday,
        startOfToday);

    List<QuizPipelineResponse> results = new ArrayList<>();

    for (QuizAttempt attempt : attempts) {
      // Skip attempts without end time (not completed)
      if (attempt.getEndTime() == null) {
        continue;
      }

      User user = attempt.getUser();
      Quiz quiz = attempt.getQuiz();

      // Calculate total questions and right answers
      ScoreResult scoreResult =
          calculateScore(quiz, convertAnswersToIntegerMap(attempt.getAnswers()));

      Map<String, Integer> detail = new HashMap<>();
      for (Map.Entry<String, Object> entry : attempt.getSkillsPoint().entrySet()) {
        if (entry.getValue() instanceof Number) {
          detail.put(entry.getKey(), ((Number) entry.getValue()).intValue());
        }
      }
      // Build response
      QuizPipelineResponse response =
          QuizPipelineResponse.builder()
              .studentId(
                  user.getStudentId() != null ? user.getStudentId() : user.getId().toString())
              .subjectTitle(quiz.getTitle())
              .totalQuestion(scoreResult.totalQuestions)
              .testTimestamp(attempt.getEndTime().toInstant())
              .totalRightAnswer(scoreResult.correctAnswers)
              .detail(detail)
              .build();

      results.add(response);
    }

    log.info("Returning {} quiz pipeline results", results.size());
    return results;
  }

  private Map<String, Integer> convertAnswersToIntegerMap(Map<String, Object> answers) {
    Map<String, Integer> result = new HashMap<>();
    if (answers != null) {
      for (Map.Entry<String, Object> entry : answers.entrySet()) {
        if (entry.getValue() instanceof Integer) {
          result.put(entry.getKey(), (Integer) entry.getValue());
        } else if (entry.getValue() instanceof Number) {
          result.put(entry.getKey(), ((Number) entry.getValue()).intValue());
        }
      }
    }
    return result;
  }

  private QuizAttemptStartResponse buildStartResponse(QuizAttempt attempt, Quiz quiz) {
    List<QuizAttemptStartResponse.QuestionDto> questions = new ArrayList<>();

    List<Object> questionsList = quiz.getQuestions();

    for (Object q : questionsList) {
      @SuppressWarnings("unchecked")
      Map<String, Object> questionMap = (Map<String, Object>) q;

      String questionId = (String) questionMap.get("id");
      String questionText = (String) questionMap.get("question");

      @SuppressWarnings("unchecked")
      List<String> skills = (List<String>) questionMap.get("skills");

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> optionsList =
          (List<Map<String, Object>>) questionMap.get("options");

      List<QuizAttemptStartResponse.OptionDto> options = new ArrayList<>();
      if (optionsList != null) {
        int optionIndex = 0;
        for (Map<String, Object> opt : optionsList) {
          options.add(
              QuizAttemptStartResponse.OptionDto.builder()
                  .optionIndex(optionIndex++)
                  .optionContent((String) opt.get("option_content"))
                  .build());
        }
      }

      questions.add(
          QuizAttemptStartResponse.QuestionDto.builder()
              .questionId(questionId == null ? null : UUID.fromString(questionId))
              .question(questionText)
              .options(options)
              .skills(skills != null ? skills : new ArrayList<>())
              .build());
    }

    return QuizAttemptStartResponse.builder()
        .attemptId(attempt.getId())
        .quizId(quiz.getId())
        .startTime(attempt.getStartTime().toInstant())
        .expiresAt(attempt.getExpiresAt().toInstant())
        .durationMinutes(quiz.getDurationMinutes())
        .questions(questions)
        .build();
  }

  private ScoreResult calculateScore(Quiz quiz, Map<String, Integer> userAnswers) {
    List<Object> questionsList = quiz.getQuestions();

    int totalQuestions = questionsList.size();
    int correctAnswers = 0;
    Map<String, List<Boolean>> skillResults = new HashMap<>();

    for (Object q : questionsList) {
      @SuppressWarnings("unchecked")
      Map<String, Object> questionMap = (Map<String, Object>) q;

      String questionId = (String) questionMap.get("id");
      @SuppressWarnings("unchecked")
      List<Object> isCorrectAnswer = (List<Object>) questionMap.get("options");
      Integer userAnswer = userAnswers.get(questionId);
      if (userAnswer == null) {
        userAnswer = -1; // treat unanswered as -1
      }
      int correctAnswerIndex = -1;
      for (Object optObj : isCorrectAnswer) {
        @SuppressWarnings("unchecked")
        Map<String, Object> optMap = (Map<String, Object>) optObj;
        Integer isCorrect = (Integer) optMap.get("is_correct_answer");
        if (Integer.valueOf(1).equals(isCorrect)) {
          correctAnswerIndex = isCorrectAnswer.indexOf(optObj); // get index of the correct answer
          break;
        }
      }
      boolean isCorrect = userAnswer == correctAnswerIndex;

      @SuppressWarnings("unchecked")
      List<String> skills = (List<String>) questionMap.get("skills");

      if (isCorrect) {
        correctAnswers++;
      }

      // Track skill results
      if (skills != null) {
        for (String skill : skills) {
          skillResults.computeIfAbsent(skill, k -> new ArrayList<>()).add(isCorrect);
        }
      }
    }

    // Calculate overall score
    double score = totalQuestions > 0 ? (correctAnswers * 100.0) / totalQuestions : 0.0;

    // Calculate skills point
    Map<String, Double> skillsPoint = new LinkedHashMap<>();
    for (Map.Entry<String, List<Boolean>> entry : skillResults.entrySet()) {
      String skill = entry.getKey();
      List<Boolean> results = entry.getValue();
      long correct = results.stream().filter(b -> b).count();
      double skillScore = (correct * 100.0) / results.size();
      skillsPoint.put(skill, Math.round(skillScore * 10.0) / 10.0); // Round to 1 decimal
    }

    return new ScoreResult(
        Math.round(score * 10.0) / 10.0, // Round to 1 decimal
        correctAnswers,
        totalQuestions,
        skillsPoint);
  }

  private String formatDuration(long seconds) {
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;

    StringBuilder sb = new StringBuilder();

    if (hours > 0) {
      sb.append(hours).append(" giờ ");
    }
    if (minutes > 0) {
      sb.append(minutes).append(" phút ");
    }
    if (secs > 0 || sb.isEmpty()) { // nếu toàn bộ là 0
      sb.append(secs).append(" giây");
    }

    return sb.toString().trim();
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizAttemptDto> getQuizAttemptsByQuizAndUser(UUID quizId, UUID userId) {
    log.info("Getting quiz attempts for quizId={}, userId={}", quizId, userId);

    // Validate quiz exists
    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + quizId));

    // Validate user has access to the quiz's class
    if (!classDomainService.isParticipantInClass(quiz.getClassEntity().getId(), userId)) {
      throw BadRequestException.message("User is not enrolled in the class of this quiz");
    }

    List<QuizAttempt> attempts = quizAttemptRepository.findAllByQuizIdAndUserId(quizId, userId);
    log.info("Found {} attempts for quizId={}, userId={}", attempts.size(), quizId, userId);

    Instant now = Instant.now();
    return attempts.stream()
        .map(
            attempt -> {
              boolean isCompleted = attempt.getEndTime() != null;
              boolean isExpired =
                  attempt.getExpiresAt() != null
                      && attempt.getExpiresAt().toInstant().isBefore(now);

              return QuizAttemptDto.builder()
                  .id(attempt.getId())
                  .quizId(attempt.getQuiz().getId())
                  .quizTitle(attempt.getQuiz().getTitle())
                  .userId(attempt.getUser().getId())
                  .userName(attempt.getUser().getEmail())
                  .startTime(attempt.getStartTime().toInstant())
                  .endTime(attempt.getEndTime() != null ? attempt.getEndTime().toInstant() : null)
                  .expiresAt(
                      attempt.getExpiresAt() != null ? attempt.getExpiresAt().toInstant() : null)
                  .score(attempt.getScore())
                  .totalPoints(attempt.getQuiz().getTotalPoints())
                  .totalQuestions(
                      attempt.getQuiz().getQuestions() != null
                          ? attempt.getQuiz().getQuestions().size()
                          : 0)
                  .skillsPoint(attempt.getSkillsPoint())
                  .isCompleted(isCompleted)
                  .isExpired(isExpired)
                  .build();
            })
        .toList();
  }

  private record ScoreResult(
      Double score,
      Integer correctAnswers,
      Integer totalQuestions,
      Map<String, Double> skillsPoint) {}
}
