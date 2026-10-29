-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: smart_canteen
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `food_items`
--

DROP TABLE IF EXISTS `food_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_items` (
  `food_id` int NOT NULL AUTO_INCREMENT,
  `food_name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `availability` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`food_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_items`
--

LOCK TABLES `food_items` WRITE;
/*!40000 ALTER TABLE `food_items` DISABLE KEYS */;
INSERT INTO `food_items` VALUES (1,'Idli',30.00,1),(2,'Dosa',50.00,1),(3,'Meals',80.00,1),(4,'Veg Biryani',100.00,1),(5,'Tea',15.00,1),(6,'Coffee',20.00,1);
/*!40000 ALTER TABLE `food_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `food_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `food_id` (`food_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`food_id`) REFERENCES `food_items` (`food_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,2,2,50.00),(3,3,2,2,50.00),(4,4,2,2,50.00),(5,5,2,2,50.00),(6,6,2,2,50.00),(7,7,2,2,50.00),(8,8,2,2,50.00),(9,9,2,2,50.00),(10,10,2,2,50.00),(11,11,2,2,50.00),(12,12,2,2,50.00),(13,13,2,2,50.00),(14,14,4,3,100.00),(15,15,2,4,50.00),(16,16,2,4,50.00),(17,17,2,4,50.00),(18,18,2,1,50.00),(19,19,2,1,50.00),(20,20,1,1,50.00),(21,21,1,1,50.00),(22,22,1,1,50.00),(23,24,1,2,50.00),(24,25,1,2,50.00),(25,30,1,6,50.00),(26,31,1,6,50.00),(27,32,1,1,30.00),(28,33,1,1,30.00),(29,34,1,1,30.00),(30,35,6,5,20.00),(31,36,2,5,50.00),(32,37,1,4,30.00),(33,38,6,10,20.00),(34,39,6,1,20.00),(35,40,4,2,100.00),(36,41,4,5,100.00),(37,42,6,1,20.00),(38,43,1,4,30.00),(39,43,5,1,15.00),(40,44,4,1,100.00),(41,45,5,1,15.00),(42,46,2,1,50.00),(43,47,3,1,80.00),(44,48,4,4,100.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` varchar(30) DEFAULT 'Pending',
  `order_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,100.00,'Served','2026-09-29 13:09:52'),(3,1,100.00,'Pending','2026-09-29 13:54:59'),(4,1,100.00,'Pending','2026-09-29 13:55:32'),(5,1,100.00,'Pending','2026-09-29 14:01:26'),(6,1,100.00,'Pending','2026-09-29 14:05:47'),(7,1,100.00,'Pending','2026-09-29 14:12:22'),(8,1,100.00,'Pending','2026-09-29 14:16:14'),(9,1,100.00,'Pending','2026-09-29 14:20:04'),(10,1,100.00,'Pending','2026-09-29 14:24:34'),(11,1,100.00,'Pending','2026-09-29 14:27:43'),(12,1,100.00,'Pending','2026-09-29 14:31:11'),(13,1,100.00,'Pending','2026-09-29 14:38:56'),(14,1,300.00,'Completed','2026-09-29 15:20:49'),(15,1,200.00,'Pending','2026-09-29 18:11:55'),(16,1,200.00,'Pending','2026-09-29 18:12:12'),(17,1,200.00,'Pending','2026-09-29 18:12:17'),(18,1,50.00,'Pending','2026-09-29 18:12:48'),(19,1,50.00,'Ready','2026-09-29 18:13:00'),(20,1,50.00,'Pending','2026-09-29 18:21:17'),(21,1,50.00,'Pending','2026-09-29 18:21:45'),(22,1,50.00,'Pending','2026-09-29 18:21:49'),(24,1,100.00,'Pending','2026-09-29 18:42:00'),(25,1,100.00,'Pending','2026-09-29 18:42:07'),(30,1,300.00,'Pending','2026-09-29 18:56:24'),(31,1,300.00,'Pending','2026-09-29 18:56:45'),(32,1,30.00,'Pending','2026-10-01 14:59:12'),(33,1,30.00,'Pending','2026-10-01 14:59:26'),(34,1,30.00,'Ready','2026-10-01 16:00:28'),(35,1,100.00,'Pending','2026-10-01 16:01:54'),(36,1,250.00,'Pending','2026-10-01 16:06:30'),(37,1,120.00,'Pending','2026-10-01 16:13:40'),(38,1,200.00,'Pending','2026-10-01 16:15:03'),(39,1,20.00,'Pending','2026-10-01 16:19:50'),(40,1,200.00,'Ready','2026-10-24 05:55:07'),(41,1,500.00,'Preparing','2026-10-24 06:01:55'),(42,2,20.00,'Served','2026-10-24 06:26:40'),(43,2,135.00,'Ready','2026-10-24 06:42:00'),(44,2,100.00,'Ready','2026-10-24 06:55:55'),(45,2,15.00,'Pending','2026-10-24 06:59:44'),(46,2,50.00,'Pending','2026-10-24 09:00:13'),(47,4,80.00,'Pending','2026-10-28 10:12:50'),(48,5,400.00,'Ready','2026-10-28 17:14:55');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tokens`
--

DROP TABLE IF EXISTS `tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tokens` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `token_number` int NOT NULL,
  `token_status` varchar(30) DEFAULT 'Waiting',
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `token_number` (`token_number`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `tokens_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tokens`
--

LOCK TABLES `tokens` WRITE;
/*!40000 ALTER TABLE `tokens` DISABLE KEYS */;
INSERT INTO `tokens` VALUES (1,1,1,'Waiting'),(3,3,2,'Waiting'),(4,4,3,'Waiting'),(5,5,4,'Waiting'),(6,6,5,'Waiting'),(7,7,6,'Waiting'),(8,8,7,'Waiting'),(9,9,8,'Waiting'),(10,10,9,'Waiting'),(11,11,10,'Waiting'),(12,12,11,'Waiting'),(13,13,12,'Waiting'),(14,11,13,'Waiting'),(15,11,14,'Waiting'),(16,11,15,'Waiting'),(17,14,16,'Waiting'),(18,15,17,'Waiting'),(19,16,18,'Waiting'),(20,17,19,'Waiting'),(21,18,20,'Waiting'),(22,19,21,'Waiting'),(23,20,22,'Waiting'),(24,21,23,'Waiting'),(25,22,24,'Waiting'),(26,24,25,'Waiting'),(27,25,26,'Waiting'),(28,30,27,'Waiting'),(29,31,28,'Waiting'),(30,32,29,'Waiting'),(31,33,30,'Waiting'),(32,34,31,'Waiting'),(33,35,32,'Waiting'),(34,36,33,'Waiting'),(35,37,34,'Waiting'),(36,38,35,'Waiting'),(37,39,36,'Waiting'),(38,40,37,'Waiting'),(39,41,38,'Waiting'),(40,42,39,'Waiting'),(41,43,40,'Waiting'),(42,44,41,'Waiting'),(43,45,42,'Waiting'),(44,46,43,'Waiting'),(45,47,44,'Waiting'),(46,48,45,'Waiting');
/*!40000 ALTER TABLE `tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Hemanth','hemanth','1234'),(2,'Nandini Reddy','Nandini','232516'),(3,'Sai sravanthi','Sai Sravanthi','1608'),(4,'Shoba','Shoba','123456'),(5,'Spurthi','spurthi','1234'),(6,'poojitha','poojitha','pooji@19');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-29 20:04:37
