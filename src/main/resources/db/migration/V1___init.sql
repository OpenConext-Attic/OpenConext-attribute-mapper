--
-- Initial tables
--
CREATE TABLE users (
  id             MEDIUMINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  unspecified_id VARCHAR(255),
  username       VARCHAR(255) NOT NULL,
  email          VARCHAR(255),
  central_idp    VARCHAR(255) NOT NULL,
  mapped         TINYINT(1)            DEFAULT 0,
  affiliations   VARCHAR(255),
  invite_hash    VARCHAR(255),
  institution    VARCHAR(255),
  created        TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_general_cs;

ALTER TABLE users ADD INDEX users_unspecified_id (unspecified_id);
