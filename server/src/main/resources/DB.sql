-- MySQL dump for BPark schema (with default data inserts)
-- Modeled after bpark_order.sql :contentReference[oaicite:0]{index=0}

USE bpark;

--
-- Table structure for table `subscribers`
--
DROP TABLE IF EXISTS `subscribers`;
CREATE TABLE IF NOT EXISTS `subscribers` (
  `subscriber_id` INT           NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(100)  NOT NULL,
  `phone`         VARCHAR(20)   DEFAULT NULL,
  `email`         VARCHAR(100)  DEFAULT NULL,
  `role`          VARCHAR(50)   DEFAULT 'USER',
  `tag`           VARCHAR(50)   DEFAULT NULL,
  `code`          INT           DEFAULT NULL,
  PRIMARY KEY (`subscriber_id`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `subscribers`
--
LOCK TABLES `subscribers` WRITE;
INSERT INTO `subscribers` 
  (`subscriber_id`,`name`,`phone`,`email`,`role`,`tag`,`code`) 
VALUES
  (2001,'Alice','050-1234567','Avigdor.Feldman@e.braude.ac.il','SUBSCRIBER',NULL,111111),
  (2002,'Bob'  ,'050-2345678','bob@example.com'  ,'SUBSCRIBER',NULL,222222),
  (2003,'Carol','050-3456789','carol@example.com','SUBSCRIBER','E200341201F82031AB12CD3F',333333),
  (2004,'Dave' ,'050-4567890','dave@example.com' ,'ATTENDANT',NULL,444444),
  (2005,'Eve'  ,'050-5678901','eve@example.com'  ,'MANAGER',NULL,555555);
UNLOCK TABLES;


--
-- Table structure for table `parking_spots`
--
DROP TABLE IF EXISTS `parking_spots`;
CREATE TABLE IF NOT EXISTS `parking_spots` (
  `spot_id`   INT            NOT NULL AUTO_INCREMENT,
  `status`    VARCHAR(20)    NOT NULL DEFAULT 'FREE',
  PRIMARY KEY (`spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `parking_spots`
--
LOCK TABLES `parking_spots` WRITE;
INSERT INTO `parking_spots` (`spot_id`,`status`) VALUES
  (101,'FREE'),
  (102,'FREE'),
  (103,'OCCUPIED'),
  (104,'FREE'),
  (105,'OCCUPIED'),
  (106,'FREE'),
  (107,'FREE'),
  (108,'OCCUPIED'),
  (109,'FREE'),
  (110,'FREE');
UNLOCK TABLES;

--
-- Table structure for table `parking_sessions`
--
DROP TABLE IF EXISTS `parking_sessions`;
CREATE TABLE IF NOT EXISTS `parking_sessions` (
  `session_id`    INT            NOT NULL AUTO_INCREMENT,
  `subscriber_id` INT            NOT NULL,
  `spot_id`       INT            NOT NULL,
  `parking_code`  INT            NOT NULL,
  `in_time`       DATETIME       NOT NULL,
  `out_time`      DATETIME       DEFAULT NULL,
  `extended`      BOOLEAN        NOT NULL DEFAULT FALSE,
  `late`          BOOLEAN        NOT NULL DEFAULT FALSE,
  `active`        BOOLEAN        NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`session_id`),
  FOREIGN KEY (`subscriber_id`) REFERENCES `subscribers`(`subscriber_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (`spot_id`) REFERENCES `parking_spots`(`spot_id`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `parking_sessions`
--
LOCK TABLES `parking_sessions` WRITE;
INSERT INTO `parking_sessions` (
  `session_id`,`subscriber_id`,`spot_id`,`parking_code`,`in_time`,`out_time`,`extended`,`late`,`active`
) VALUES
  (1,2001,103,123001,'2025-05-01 08:00:00','2025-05-01 12:00:00',FALSE,FALSE,TRUE),
  (2,2002,105,123002,'2025-05-02 09:30:00',NULL, FALSE,FALSE,TRUE),
  (3,2003,108,123003,'2025-05-03 10:15:00','2025-05-03 15:45:00',TRUE, FALSE,FALSE);
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE IF NOT EXISTS `reservations` (
  `reservation_id`  INT            NOT NULL AUTO_INCREMENT,
  `subscriber_id`   INT            NOT NULL,
  `spot_id`         INT            NOT NULL,
  `date`            DATE           NOT NULL,
  `start_time`      TIME           DEFAULT NULL,
  `end_time`        TIME           DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`subscriber_id`) REFERENCES `subscribers`(`subscriber_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (`spot_id`) REFERENCES `parking_spots`(`spot_id`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reservations`
--
LOCK TABLES `reservations` WRITE;
INSERT INTO `reservations` (
  `reservation_id`,`subscriber_id`,`spot_id`,`date`,`start_time`,`end_time`
) VALUES
  (1,2002,101,'2025-05-10','09:00:00','11:00:00'),
  (2,2003,102,'2025-05-11','14:30:00','16:30:00'),
  (3,2004,104,'2025-05-12','08:15:00','10:15:00'),
  (4,2005,110,'2025-05-13','12:00:00','14:00:00'),
  (5,2001,109,'2025-05-14','10:00:00','12:00:00');
UNLOCK TABLES;