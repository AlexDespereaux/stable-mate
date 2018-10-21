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
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES
  (1,'img0026.jpg','description','notes',1533731244,-37.7195230000,145.0459100000,1.34456,342.00000,9,5),
  (2,'img0003.jpg','apple cell','with dye',1523731244,-37.7195230000,145.0459100000,1.34456,342.00000,9,NULL),
  (3,'img0005.jpg','human muscle tissue','',1533431244,-37.7195230000,145.0459100000,1.34456,342.00000,9,NULL),
  (4,'img0292.jpg','worm cell wall','',1531731244,-37.7195230000,145.0459100000,1.34456,342.00000,9,NULL),
  (5,'img3636.jpg','pancreatic cell','normal',1433731244,-37.7195230000,145.0459100000,1.34456,342.00000,9,NULL),
  (6,'img3333.jpg','celery','under uv light',1533751244,-37.7195230000,145.0459100000,1.34456,342.00000,9,NULL),
  (7,'img0112.jpg','private sample','',1524307164,181.0000000000,181.0000000000,1486.49215,0.47503,10,NULL),
  (8,'img0132.jpg','pancreatic cell','tumerous',1524307164,181.0000000000,181.0000000000,1486.49215,0.47503,10,NULL),
  (9,'img0125.jpg','HeLa','',1524307164,181.0000000000,181.0000000000,37.16230,26.80169,10,NULL),
  (10,'img1000.jpg','skin cell','with green dye',1524307164,181.0000000000,181.0000000000,57172.32723,0.01452,10,NULL);
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `legend`
--

LOCK TABLES `legend` WRITE;
/*!40000 ALTER TABLE `legend` DISABLE KEYS */;
INSERT INTO `legend` VALUES
  (10,1,'star','cell wall'),
  (11,1,'triangle','nucleus'),
  (12,1,'star','cell wall'),
  (13,1,'triangle','nucleus');
/*!40000 ALTER TABLE `legend` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
  (1,'$2a$10$2Bt1cfLDfKxpD7GmuN/TE.4WPqw1lRhPSwpHjSkA7s0f6mursAKZy','richard@test.com',1),
  (2,'$2a$10$Ocqnfky.r6gWd9Q2M9hrvuswbNY1TxPunmvN2ScFSsqQQtx1PDKKq','marita@test.com',1),
  (3,'$2a$10$cUZr5SVr2ptNKM4vNYxVV.eIKH1p9csoKFwAGuOiOpMfY9ouUXbB.','alex@test.com',1),
  (4,'$2a$10$1xt71keywUlzrGlgq1cpbuPuta6WZ7GaTYHk3i/LuToYgP3M2BuvC','jackson@test.com',1),
  (5,'$2a$10$qrjztR8Bwidh1R2dNTFmhecCoxB7nqXi115x.8bNE3peDfxU.Tu42','anmol@test.com',1),
  (6,'$2b$12$YTv1eFSNBqjQtf6t2Sol2eaBjvpcs0tGFEIrrkVm5BApPyUcC1Hji','oliver@test.com',0),
  (7,'$2b$12$dqXy2ATdqLgM68LnLRuzSOKxWHx6uJbUVJuKoW0xJRIQRXITB43Ie','william@test.com',0),
  (8,'$2b$12$D9B1u5Vl6aM2NRPOcXpbB.m8c5h/dNyYw1X56bjy/kyjWLT8IMW42','jack@test.com',0),
  (9,'$2b$12$Ur254EzDaiJFBiuoJyG4yecY.jwLhrptJcdylekp7AdhNxmHmNkea','noah@test.com',0),
  (10,'$2b$12$ZfHEWWviU5JB1jTYW0AVPuZrRgu61TS5LtnjtLtPG0i4IEeNVc9we','james@test.com',0),
  (11,'$2b$12$xfWpw9/2DdDEFbopp72OfO8fDXf3hzE87i9rPdxJVAe8GgdOtbLH2','thomas@test.com',0),
  (12,'$2b$12$xCu.6/8R9C61FYWcmKXRluuZIxQl5U9gqdcqt6x9pzosxQ8rcNciG','ethan@test.com',0),
  (13,'$2b$12$844vDM.cv929y0HRVTyMrux8xHwkZ8HRTSsnThZd4LNI23W/N70qK','lucas@test.com',0),
  (14,'$2b$12$DbKSIz0cbOm.gUlIgM6bj.7faHagGRbcFzEs11ioKn49Kk0LU9a6O','lachlan@test.com',0);
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

-- Dump completed on 2018-10-21 17:18:11
