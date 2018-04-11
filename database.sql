-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pzyko_user` (
  `uuid` CHAR(36) NOT NULL,
  `username` VARCHAR(16) NOT NULL,
  `last_seen` BIGINT NOT NULL DEFAULT -1,
  PRIMARY KEY (`uuid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `claim`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pzyko_claim` (
  `id` INT NOT NULL,
  `world` VARCHAR(45) NOT NULL,
  `min_x` INT NOT NULL,
  `min_z` INT NOT NULL,
  `max_x` INT NOT NULL,
  `max_z` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `claim_role_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pzyko_claim_role_permission` (
  `rolename` ENUM('ACCESS', 'CONTAINER', 'BUILD', 'MANAGE') NOT NULL,
  `permission` VARCHAR(45) NOT NULL,
  `value` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`rolename`, `permission`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `claim_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pzyko_claim_user_role` (
  `claim_id` INT NOT NULL,
  `user_uuid` CHAR(36) NOT NULL,
  `rolepermission` ENUM('ACCESS', 'CONTAINER', 'BUILD', 'MANAGE') NOT NULL,
  PRIMARY KEY (`claim_id`, `user_uuid`, `rolepermission`),
  INDEX `pzyko_fk_claim_has_user_user1_idx` (`user_uuid` ASC),
  INDEX `pzyko_fk_claim_has_user_claim_idx` (`claim_id` ASC),
  INDEX `pzyko_fk_role_rolepermission1_idx` (`rolepermission` ASC),
  CONSTRAINT `pzyko_fk_claim_has_user_claim`
    FOREIGN KEY (`claim_id`)
    REFERENCES `pzyko_claim` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `pzyko_fk_claim_has_user_user1`
    FOREIGN KEY (`user_uuid`)
    REFERENCES `pzyko_user` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `pzyko_fk_role_rolepermission1`
    FOREIGN KEY (`rolepermission`)
    REFERENCES `pzyko_claim_role_permission` (`rolename`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `claim_flag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pzyko_claim_flag` (
  `claim_id` INT NOT NULL,
  `flag` VARCHAR(45) NOT NULL,
  `value` VARCHAR(45) NULL,
  PRIMARY KEY (`claim_id`, `flag`),
  INDEX `pzyko_fk_claim_flag_claim1_idx` (`claim_id` ASC),
  CONSTRAINT `pzyko_fk_claim_flag_claim1`
    FOREIGN KEY (`claim_id`)
    REFERENCES `pzyko_claim` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
