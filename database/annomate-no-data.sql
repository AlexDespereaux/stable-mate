-- MySQL dump 10.16  Distrib 10.1.30-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: aa1i7gl7max280m.ccg40tjk5pex.ap-southeast-2.rds.amazonaws.com    Database: annomate
-- ------------------------------------------------------
-- Server version	5.7.22-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
  `imageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(100) NOT NULL,
  `description` text,
  `notes` text,
  `datetime` int(10) unsigned NOT NULL,
  `latitude` decimal(20,10) DEFAULT NULL,
  `longitude` decimal(20,10) DEFAULT NULL,
  `dFov` decimal(10,5) DEFAULT NULL,
  `ppm` decimal(10,5) DEFAULT NULL,
  `userId` int(10) unsigned NOT NULL,
  `rating` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`imageId`),
  KEY `userId` (`userId`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `legend`
--

DROP TABLE IF EXISTS `legend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `legend` (
  `legendId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imageId` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`legendId`),
  KEY `imageId` (`imageId`),
  CONSTRAINT `legend_ibfk_1` FOREIGN KEY (`imageId`) REFERENCES `images` (`imageId`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `userId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `password` char(60) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `admin` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-10-21 17:10:13
