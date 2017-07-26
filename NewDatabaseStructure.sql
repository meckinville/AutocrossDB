-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: autocrossdb2
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cars`
--

DROP TABLE IF EXISTS `cars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cars` (
  `car_make` varchar(50) NOT NULL,
  `car_country` varchar(45) NOT NULL,
  `car_alternatives` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`car_make`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cars`
--

LOCK TABLES `cars` WRITE;
/*!40000 ALTER TABLE `cars` DISABLE KEYS */;
INSERT INTO `cars` VALUES ('ACURA','JAPAN','ACRUA'),('ASTON MARTIN','GREAT BRITAIN','ASTON'),('AUDI','GERMANY',NULL),('BMW','GERMANY','M3, M5'),('BUICK','USA',NULL),('CADILLAC','USA','CADDY,CADILAC'),('CHEVROLET','USA','CHEVY,CORVETTE,C5,C6,CAMARO,VETTE,CORVETE,CHECY,Z06,CAMERO'),('CHRYSLER','USA',NULL),('DATSUN','JAPAN',NULL),('DODGE','USA','VIPER'),('FERRARI','ITALY',NULL),('FIAT','ITALY',NULL),('FORD','USA','MUSTANG,BOSS,FOCUS,SVT'),('HONDA','JAPAN','CIVIC,S2000,S2K,HOND,CRX'),('HYUNDAI','KOREA','HUNDAI'),('INFINITI','JAPAN','INFINITY,INFINIT'),('INVADER','USA',NULL),('JAGUAR','GREAT BRITAIN','JAG'),('KIA','KOREA',NULL),('LAMBORGHINI','ITALY','LAMBO'),('LEXUS','JAPAN',NULL),('LOTUS','GREAT BRITAIN',NULL),('MAZDA','JAPAN','MAZDASPEED,MADZA,MAXDA,MIATA,MX5,MAZADA,RX7,RX8,RX-7,RX-8'),('MERCEDES','GERMANY','MERCEDES BENZ,MERCEDES-BENZ,MBENZ,AMG'),('MINI','GREAT BRITAIN','MINMI,COOPER'),('MITSUBISHI','JAPAN','EVO,LANCER'),('NISSAN','JAPAN','NISMO,NISSIAN,NISSON'),('PONTIAC','USA',NULL),('PORSCHE','GERMANY','PORCHE,CAYMAN,911,BOXSTER,BOXTER,CAMAN'),('QRE','USA',NULL),('RAPTOR','USA',NULL),('SAAB','GERMANY',NULL),('SATURN','USA',NULL),('SCION','JAPAN','FR-S,FRS'),('STALKER','USA','STLAKER'),('SUBARU','JAPAN','SUBI,SUB,WRX,BRZ,STI'),('TOYOTA','JAPAN','MR2'),('VOLKSWAGEN','GERMANY','VW,GTI,GOLF,VOLKSWAGON'),('VOLVO','SWEDEN','VOLVA');
/*!40000 ALTER TABLE `cars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classes` (
  `CLASS_NAME` varchar(20) NOT NULL,
  `CLASS_2016_PAX` double NOT NULL,
  `CLASS_2015_PAX` double NOT NULL,
  `CLASS_2014_PAX` double NOT NULL,
  `CLASS_2013_PAX` double NOT NULL,
  `CLASS_2017_PAX` double NOT NULL,
  PRIMARY KEY (`CLASS_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES ('AM',1,1,1,1,1),('AS',0.833,0.829,0.833,0.847,0.819),('ASP',0.865,0.865,0.866,0.866,0.856),('ASR',0,0,0.848,0,0),('BM',0.966,0.964,0.965,0.962,0.956),('BP',0.883,0.881,0.881,0.878,0.869),('BS',0.826,0.826,0.831,0.845,0.813),('BSP',0.863,0.863,0.863,0.858,0.853),('BSR',0,0,0.845,0,0),('CAM',0.839,0.83,0.83,0.83,0.823),('CAM-C',0.839,0.83,0.83,0.83,0.823),('CAM-S',0.848,0.836,0.836,0.836,0.838),('CAM-T',0.834,0.825,0.825,0.825,0.817),('CM',0.916,0.919,0.922,0.91,0.901),('CP',0.864,0.864,0.864,0.862,0.854),('CS',0.819,0.814,0.821,0.834,0.81),('CSP',0.867,0.861,0.861,0.861,0.86),('CSR',0,0,0.834,0,0),('DM',0.919,0.918,0.92,0.918,0.906),('DP',0.879,0.879,0.879,0.874,0.865),('DS',0.811,0.812,0.815,0.826,0.801),('DSP',0.855,0.854,0.855,0.855,0.842),('DSR',0,0,0.825,0,0),('EM',0.92,0.922,0.926,0.928,0.905),('EP',0.871,0.874,0.876,0.875,0.859),('ES',0.807,0.808,0.814,0.828,0.794),('ESP',0.852,0.849,0.849,0.849,0.837),('ESR',0,0,0.828,0,0),('FM',0.926,0.926,0.924,0.917,0.916),('FP',0.88,0.88,0.883,0.877,0.873),('FS',0.814,0.81,0.818,0.83,0.804),('FSAE',0.982,0.984,0.989,0.989,0.966),('FSP',0.84,0.84,0.839,0.838,0.829),('FSR',0,0,0.83,0,0),('GS',0.806,0.806,0.806,0.816,0.793),('GSR',0,0,0.812,0,0),('HCR',0.838,0,0,0,0),('HCS',0.817,0,0,0,0.809),('HS',0.798,0.796,0.797,0.804,0.786),('HSR',0,0,0.804,0,0),('JA',0.878,0.878,0.88,0.879,0.864),('JB',0.842,0.842,0.842,0.842,0.834),('JC',0.734,0.734,0.741,0.743,0.726),('KM',0.954,0.955,0.957,0.955,0.939),('NR',1,1,1,1,1),('NS',1,1,1,1,1),('S1A',1,1,1,1,1),('S2A',1,1,1,1,1),('S2B',1,1,1,1,1),('S3',1,1,1,1,1),('SL',1,1,1,1,1),('SM',0.87,0.87,0.87,0.867,0.861),('SMF',0.861,0.853,0.851,0.852,0.848),('SMST2',0.858,0.858,0.858,0.858,0.858),('SMST4',0.856,0.856,0.856,0.856,0.858),('SS',0.835,0.835,0.843,0.859,0.826),('SSM',0.882,0.882,0.882,0.881,0.875),('SSP',0.872,0.871,0.872,0.871,0.862),('SSR',0.86,0.859,0.86,0,0.847),('STC',0,0,0.824,0.824,0),('STF',0.809,0.801,0.801,0.795,0.8),('STO',0.862,0.862,1,1,0.862),('STP',0.837,0,0,0,0.82),('STR',0.841,0.838,0.838,0.839,0.83),('STS',0.832,0.828,0.829,0.829,0.818),('STU',0.845,0.844,0.846,0.846,0.831),('STX',0.836,0.831,0.827,0.827,0.822),('XP',0.907,0.905,0.906,0.901,0.892);
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver_stats`
--

DROP TABLE IF EXISTS `driver_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `driver_stats` (
  `year` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `class` varchar(5) NOT NULL,
  `events` int(11) NOT NULL,
  `rawWins` int(11) NOT NULL,
  `paxWins` int(11) NOT NULL,
  `cones` int(11) NOT NULL,
  `offcourses` int(11) NOT NULL,
  `runs` int(11) NOT NULL,
  PRIMARY KEY (`year`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driver_stats`
--

LOCK TABLES `driver_stats` WRITE;
/*!40000 ALTER TABLE `driver_stats` DISABLE KEYS */;
/*!40000 ALTER TABLE `driver_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `eventId` int(11) NOT NULL AUTO_INCREMENT,
  `eventDate` datetime NOT NULL,
  `eventClub` varchar(45) NOT NULL,
  `eventLocation` varchar(45) NOT NULL,
  `evenType` varchar(1) NOT NULL,
  `eventPaxWinner` varchar(50) NOT NULL,
  `eventRawWinner` varchar(50) NOT NULL,
  `eventDrivers` int(11) NOT NULL,
  `eventRunsPer` int(11) NOT NULL,
  `eventCones` int(11) NOT NULL,
  PRIMARY KEY (`eventId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `runs`
--

DROP TABLE IF EXISTS `runs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runs` (
  `runId` int(11) NOT NULL AUTO_INCREMENT,
  `runEventId` int(11) NOT NULL,
  `runDriver` varchar(50) NOT NULL,
  `runCar` varchar(45) NOT NULL,
  `runClass` varchar(5) NOT NULL,
  `runNumber` int(11) NOT NULL,
  `runTime` double NOT NULL,
  `runPaxTime` double NOT NULL,
  `runOffcourse` varchar(1) NOT NULL,
  `runCones` int(11) NOT NULL,
  PRIMARY KEY (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `runs`
--

LOCK TABLES `runs` WRITE;
/*!40000 ALTER TABLE `runs` DISABLE KEYS */;
/*!40000 ALTER TABLE `runs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-07-26 16:22:25
