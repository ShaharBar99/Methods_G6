-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: bpark
-- ------------------------------------------------------
-- Server version	8.0.41

USE bpark;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_number` int NOT NULL AUTO_INCREMENT,
  `parking_space` int DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `confirmation_code` int DEFAULT NULL,
  `subscriber_id` int DEFAULT NULL,
  `date_of_placing_an_order` date DEFAULT NULL,
  PRIMARY KEY (`order_number`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
INSERT INTO `order` VALUES (1,101,'2025-05-01',123456,2001,'2025-04-30'),(2,102,'2025-05-01',234567,2002,'2025-04-30'),(3,103,'2025-05-01',345678,2003,'2025-05-01'),(4,104,'2025-05-04',456789,2004,'2025-05-02'),(5,105,'2025-05-05',567890,2005,'2025-05-03');
UNLOCK TABLES;

-- Dump completed on 2025-05-10 22:35:39
