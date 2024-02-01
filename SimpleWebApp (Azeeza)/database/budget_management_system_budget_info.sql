-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: budget_management_system
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `budget_info`
--

DROP TABLE IF EXISTS `budget_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget_info` (
  `budget_id` int NOT NULL AUTO_INCREMENT,
  `budget_type` varchar(100) DEFAULT NULL,
  `budget_date` datetime DEFAULT NULL,
  `budget_amount` double DEFAULT NULL,
  `budget_remarks` varchar(255) DEFAULT NULL,
  `budget_status` varchar(50) DEFAULT NULL,
  `budget_totalAmount` double DEFAULT NULL,
  PRIMARY KEY (`budget_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budget_info`
--

LOCK TABLES `budget_info` WRITE;
/*!40000 ALTER TABLE `budget_info` DISABLE KEYS */;
INSERT INTO `budget_info` VALUES (1,'Training and Development Budget','2023-07-10 16:28:41',150,'Programs Fee','Approved',NULL),(4,'Operation Budget','2023-07-10 17:54:02',200,'Utilities','Rejected',NULL),(5,'Research and Development Budget','2023-07-11 11:31:06',300,'Benefits for Testers\n(3 person, each RM100)','Approved',NULL),(6,'Travel and Expenses Budget','2023-07-11 17:01:48',100,'Business Trip for 1 day','Approved',NULL),(7,'Marketing Budget','2023-07-12 11:16:37',50,'Social Media Influencer benefits','Rejected',NULL),(10,'Travel and Expenses Budget','2023-07-12 13:44:47',100,'Meeting with client in Pulau Pinang for 1 day.','Approved',NULL),(14,'Sales and Revenue Budget','2023-07-14 13:45:35',50,'Discounts ','Rejected',NULL),(15,'Operation Budget','2023-07-14 14:16:45',50,'Utensils','Approved',NULL),(20,'Operation Budget','2023-07-14 14:46:04',20,'Utensils','Approved',NULL),(23,'Travel and Expenses Budget','2023-07-14 15:00:07',100,'Meeting with Client ','Approved',NULL),(26,'Training and Development Budget','2023-07-14 16:28:33',50,'Seminar Fee','Rejected',NULL),(28,'Training and Development Budget','2023-07-17 09:43:02',150,'Training Workshop Fee','Approved',NULL),(29,'Marketing Budget','2023-08-04 14:22:23',100,'Test','Pending',NULL);
/*!40000 ALTER TABLE `budget_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-05 10:31:48
