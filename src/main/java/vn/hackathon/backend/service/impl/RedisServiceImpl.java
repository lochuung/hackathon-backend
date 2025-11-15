package vn.hackathon.backend.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.hackathon.backend.service.RedisService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

  private static final String ATTEMPT_ANSWERS_PREFIX = "quiz:attempt:answers:";

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void saveAttemptAnswers(UUID attemptId, Map<String, Integer> answers, long ttlMinutes) {
    String key = ATTEMPT_ANSWERS_PREFIX + attemptId.toString();
    try {
      redisTemplate.opsForValue().set(key, answers, ttlMinutes, TimeUnit.MINUTES);
      log.info("Saved answers for attempt {} to Redis with TTL {} minutes", attemptId, ttlMinutes);
    } catch (Exception e) {
      log.error("Failed to save answers to Redis for attempt {}", attemptId, e);
      throw new RuntimeException("Failed to save answers to cache", e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Integer> getAttemptAnswers(UUID attemptId) {
    String key = ATTEMPT_ANSWERS_PREFIX + attemptId.toString();
    try {
      Object value = redisTemplate.opsForValue().get(key);
      if (value == null) {
        log.debug("No cached answers found for attempt {}", attemptId);
        return null;
      }
      return (Map<String, Integer>) value;
    } catch (Exception e) {
      log.error("Failed to get answers from Redis for attempt {}", attemptId, e);
      return null;
    }
  }

  @Override
  public void deleteAttemptAnswers(UUID attemptId) {
    String key = ATTEMPT_ANSWERS_PREFIX + attemptId.toString();
    try {
      redisTemplate.delete(key);
      log.info("Deleted cached answers for attempt {}", attemptId);
    } catch (Exception e) {
      log.error("Failed to delete answers from Redis for attempt {}", attemptId, e);
    }
  }

  @Override
  public boolean hasAttemptAnswers(UUID attemptId) {
    String key = ATTEMPT_ANSWERS_PREFIX + attemptId.toString();
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      log.error("Failed to check if answers exist in Redis for attempt {}", attemptId, e);
      return false;
    }
  }
}
