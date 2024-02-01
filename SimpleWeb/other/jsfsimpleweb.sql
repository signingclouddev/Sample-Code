-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: jsfsimpleweb
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(255) NOT NULL,
  `LastName` varchar(255) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Username` varchar(255) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `PhoneNumber` varchar(20) DEFAULT NULL,
  `Balance` int DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (8,'james','wong','jameswong@gmail.com','james123','91ba5b47882a947199d792829420ec7a5f171c8398189dc445f791b7d4aba0d5f90feb58a1627b89a74ef96c8bb2faa739ff82d906e03786ae255f0624a1857b',NULL,0),(9,'summer','you','summeryou@gmail.com','summer00','436a4d9c3d8811277fccfea840d6cfd0c6459865849f3dc9648ec5f572b886a7920767e7f7089480d6e6972ccb3061a3ba0a353f69891dab77d7ea1c221a87ca',NULL,8),(10,'jayden','lim','jayden99@gmail.com','jayden99','b8a625050a9bcd9741d888e265ae79e372574947be3ae918ded25d8ae5d033bd16140ff351a9674c8cf0507019f803f433ec736a0d1d0384cf9592356a38f90d',NULL,0),(11,'johnnie','walker','johnniewalker123@gmail.com','johhnnie','91ba5b47882a947199d792829420ec7a5f171c8398189dc445f791b7d4aba0d5f90feb58a1627b89a74ef96c8bb2faa739ff82d906e03786ae255f0624a1857b',NULL,0),(12,'patrick','john','patrick90@gmail.com','patrick90','91ba5b47882a947199d792829420ec7a5f171c8398189dc445f791b7d4aba0d5f90feb58a1627b89a74ef96c8bb2faa739ff82d906e03786ae255f0624a1857b',NULL,0),(13,'johnnie','wallker','johnniewalker@gmail.com','johnniewalker','91ba5b47882a947199d792829420ec7a5f171c8398189dc445f791b7d4aba0d5f90feb58a1627b89a74ef96c8bb2faa739ff82d906e03786ae255f0624a1857b',NULL,0),(26,'jimmy','chew','jchew00@gmail.com','jchew00','ec61a7c0d58a336cb65a1003dcc65dbba327003e4b4c4bf4591df7bccd89f3ce6a0b017a8567450a50b9ec400875b7a9278c9af5984af60959686c48efd4dccc','+601156883923',29),(27,'someone','else','someone00@gmail.com','someone00','1204f02eaf32557852e1336eee84824a75b0f24c373a87b92783162d0aca0af75767e3ed169f6ed5d2058e938c7a365af681205da76f41ccb88a00135b1a4942','+60123456789',2),(41,'james','wong','jameswong00@gmail.com','jameswong00','4ca8c502e062f3456c7f7d793c592acbb50527dc30d707d01822df0ecfdc718fc8fa72282d9926018cade9669e6c3a4ecaefc0034d301a125073164827e00145','+601127771234',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-11 17:20:13
