package de.x132.ahp.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * User status enumeration.
 *
 * @author Max Wick
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserStatus {
  ACTIVE,
  INACTIVE,
  PENDING_ACTIVATION,
  LOCKED
}
