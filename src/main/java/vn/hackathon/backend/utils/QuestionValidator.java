package vn.hackathon.backend.utils;

import java.util.List;
import java.util.Map;
import vn.hackathon.backend.exception.BadRequestException;

/**
 * Validator for quiz question structure
 *
 * <p>Expected question structure: { "question": "Question text", "options": [ { "option_content":
 * "Option text", "is_correct_answer": 0 or 1 } ], "skills": ["skill1", "skill2"] }
 */
public class QuestionValidator {

  private static final int MIN_OPTIONS = 2;
  private static final int MAX_OPTIONS = 5;
  private static final String QUESTION_FIELD = "question";
  private static final String OPTIONS_FIELD = "options";
  private static final String SKILLS_FIELD = "skills";
  private static final String OPTION_CONTENT_FIELD = "option_content";
  private static final String IS_CORRECT_ANSWER_FIELD = "is_correct_answer";

  /**
   * Validate a list of questions
   *
   * @param questions the list of questions to validate
   * @throws BadRequestException if any question is invalid
   */
  public static void validateQuestions(List<Object> questions) {
    if (questions == null || questions.isEmpty()) {
      throw BadRequestException.message("Quiz must have at least one question");
    }

    for (int i = 0; i < questions.size(); i++) {
      validateQuestion(questions.get(i), i);
    }

    validateCorrectAnswerCount(questions);
  }

  /**
   * Validate a single question
   *
   * @param question the question object to validate
   * @param index the index of the question in the list
   * @throws BadRequestException if the question is invalid
   */
  @SuppressWarnings("unchecked")
  private static void validateQuestion(Object question, int index) {
    if (!(question instanceof Map)) {
      throw BadRequestException.message(
          String.format("Question at index %d must be a JSON object", index));
    }

    Map<String, Object> questionMap = (Map<String, Object>) question;

    // Validate question text
    if (!questionMap.containsKey(QUESTION_FIELD)) {
      throw BadRequestException.message(
          String.format("Question at index %d is missing 'question' field", index));
    }

    Object questionText = questionMap.get(QUESTION_FIELD);
    if (!(questionText instanceof String) || ((String) questionText).trim().isEmpty()) {
      throw BadRequestException.message(
          String.format("Question text at index %d cannot be empty", index));
    }

    // Validate options
    if (!questionMap.containsKey(OPTIONS_FIELD)) {
      throw BadRequestException.message(
          String.format("Question at index %d is missing 'options' field", index));
    }

    Object optionsObj = questionMap.get(OPTIONS_FIELD);
    if (!(optionsObj instanceof List)) {
      throw BadRequestException.message(
          String.format("'options' in question at index %d must be a list", index));
    }

    List<Object> options = (List<Object>) optionsObj;
    validateOptions(options, index);

    // Validate skills if present
    if (questionMap.containsKey(SKILLS_FIELD)) {
      Object skillsObj = questionMap.get(SKILLS_FIELD);
      if (!(skillsObj instanceof List)) {
        throw BadRequestException.message(
            String.format("'skills' in question at index %d must be a list", index));
      }

      List<Object> skills = (List<Object>) skillsObj;
      for (int i = 0; i < skills.size(); i++) {
        if (!(skills.get(i) instanceof String) || ((String) skills.get(i)).trim().isEmpty()) {
          throw BadRequestException.message(
              String.format("Skill at index %d in question %d cannot be empty", i, index));
        }
      }
    }
  }

  /**
   * Validate the options list
   *
   * @param options the options list to validate
   * @param questionIndex the index of the question
   * @throws BadRequestException if options are invalid
   */
  @SuppressWarnings("unchecked")
  private static void validateOptions(List<Object> options, int questionIndex) {
    if (options.size() < MIN_OPTIONS) {
      throw BadRequestException.message(
          String.format(
              "Question at index %d must have at least %d options", questionIndex, MIN_OPTIONS));
    }

    if (options.size() > MAX_OPTIONS) {
      throw BadRequestException.message(
          String.format(
              "Question at index %d can have at most %d options", questionIndex, MAX_OPTIONS));
    }

    int correctAnswerCount = 0;
    for (int i = 0; i < options.size(); i++) {
      Object option = options.get(i);

      if (!(option instanceof Map)) {
        throw BadRequestException.message(
            String.format(
                "Option at index %d in question %d must be a JSON object", i, questionIndex));
      }

      Map<String, Object> optionMap = (Map<String, Object>) option;

      // Validate option_content
      if (!optionMap.containsKey(OPTION_CONTENT_FIELD)) {
        throw BadRequestException.message(
            String.format(
                "Option at index %d in question %d is missing 'option_content' field",
                i, questionIndex));
      }

      Object optionContent = optionMap.get(OPTION_CONTENT_FIELD);
      if (!(optionContent instanceof String) || ((String) optionContent).trim().isEmpty()) {
        throw BadRequestException.message(
            String.format(
                "Option content at index %d in question %d cannot be empty", i, questionIndex));
      }

      // Validate is_correct_answer
      if (!optionMap.containsKey(IS_CORRECT_ANSWER_FIELD)) {
        throw BadRequestException.message(
            String.format(
                "Option at index %d in question %d is missing 'is_correct_answer' field",
                i, questionIndex));
      }

      Object isCorrectAnswer = optionMap.get(IS_CORRECT_ANSWER_FIELD);
      if (!(isCorrectAnswer instanceof Number)) {
        throw BadRequestException.message(
            String.format(
                "is_correct_answer at index %d in question %d must be a number (0 or 1)",
                i, questionIndex));
      }

      int correctValue = ((Number) isCorrectAnswer).intValue();
      if (correctValue != 0 && correctValue != 1) {
        throw BadRequestException.message(
            String.format(
                "is_correct_answer at index %d in question %d must be 0 or 1", i, questionIndex));
      }

      if (correctValue == 1) {
        correctAnswerCount++;
      }
    }

    if (correctAnswerCount == 0) {
      throw BadRequestException.message(
          String.format(
              "Question at index %d must have at least one correct answer", questionIndex));
    }
  }

  /**
   * Validate that there is exactly one correct answer per question
   *
   * @param questions the list of questions
   * @throws BadRequestException if a question has multiple correct answers
   */
  @SuppressWarnings("unchecked")
  private static void validateCorrectAnswerCount(List<Object> questions) {
    for (int i = 0; i < questions.size(); i++) {
      Map<String, Object> questionMap = (Map<String, Object>) questions.get(i);
      List<Object> options = (List<Object>) questionMap.get(OPTIONS_FIELD);

      int correctCount = 0;
      for (Object optionObj : options) {
        Map<String, Object> option = (Map<String, Object>) optionObj;
        int correctValue = ((Number) option.get(IS_CORRECT_ANSWER_FIELD)).intValue();
        if (correctValue == 1) {
          correctCount++;
        }
      }

      if (correctCount > 1) {
        throw BadRequestException.message(
            String.format(
                "Question at index %d has %d correct answers, but only 1 is allowed",
                i, correctCount));
      }
    }
  }

  /**
   * Validate skills list
   *
   * @param skills the skills list to validate
   * @throws BadRequestException if skills are invalid
   */
  public static void validateSkills(List<String> skills) {
    if (skills == null) {
      throw BadRequestException.message("Skills list cannot be null");
    }

    for (int i = 0; i < skills.size(); i++) {
      String skill = skills.get(i);
      if (skill == null || skill.trim().isEmpty()) {
        throw BadRequestException.message(String.format("Skill at index %d cannot be empty", i));
      }
    }
  }
}
