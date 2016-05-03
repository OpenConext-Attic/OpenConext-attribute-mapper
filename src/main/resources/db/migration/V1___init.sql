--
-- Initial tables
--
CREATE TABLE users (
  id             MEDIUMINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  unspecified_id VARCHAR(255) NOT NULL,
  name           VARCHAR(255) NOT NULL,
  email          VARCHAR(255) NOT NULL,
  central_idp    VARCHAR(255) NOT NULL,
  affiliations   VARCHAR(255)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_general_cs;

ALTER TABLE users ADD INDEX users_unspecified_id (unspecified_id);
