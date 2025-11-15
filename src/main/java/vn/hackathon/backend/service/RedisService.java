package vn.hackathon.backend.service;

import java.util.Map;
import java.util.UUID;

public interface RedisService {
  /**
   * Save quiz attempt answers to Redis
   *
   * @param attemptId the attempt ID
   * @param answers the answers map
   * @param ttlMinutes time to live in minutes
   */
  void saveAttemptAnswers(UUID attemptId, Map<String, Integer> answers, long ttlMinutes);

  /**
   * Get quiz attempt answers from Redis
   *
   * @param attemptId the attempt ID
   * @return the answers map
   */
  Map<String, Integer> getAttemptAnswers(UUID attemptId);

  /**
   * Delete quiz attempt answers from Redis
   *
   * @param attemptId the attempt ID
   */
  void deleteAttemptAnswers(UUID attemptId);

  /**
   * Check if attempt answers exist in Redis
   *
   * @param attemptId the attempt ID
   * @return true if exists
   */
  boolean hasAttemptAnswers(UUID attemptId);
}
