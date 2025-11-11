package lumina.snapshot.authservice.exception;

import lumina.snapshot.authservice.dto.ErrorCode;

public class RateLimitException extends RuntimeException {
  private final String errorCode;
  private final int retryAfterSeconds;

  public RateLimitException(String message, int retryAfterSeconds) {
    super(message);
    this.errorCode = ErrorCode.RATE_LIMIT_EXCEEDED.getCode();
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public int getRetryAfterSeconds() {
    return retryAfterSeconds;
  }
}
