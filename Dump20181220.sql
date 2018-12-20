-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: axdb
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `classes` (
  `CLASS_NAME` varchar(10) NOT NULL,
  `CLASS_DESCRIPTION` varchar(100) DEFAULT NULL,
  `CLASS_SUBTYPE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`CLASS_NAME`),
  UNIQUE KEY `CLASS_NAME_UNIQUE` (`CLASS_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES ('AM','A Modified','Modified'),('AS','A Street','Street'),('ASP','A Street Prepared','Street Prepared'),('ASR','A Street Race','Street Race'),('BM','B Modified','Modified'),('BP','B Prepared','Prepared'),('BS','B Street','Street'),('BSP','B Street Prepared','Street Prepared'),('BSR','B Street Race','Street Race'),('CAM','Classic American Muscle','Classic American Muscle'),('CAM-C','Classic American Muscle Contemporary','Classic American Muscle'),('CAM-S','Classic American Muscle Sport','Classic American Muscle'),('CAM-T','Classic American Muscle Traditional','Classic American Muscle'),('CM','C Modified','Modified'),('CP','C Prepared','Prepared'),('CS','C Street','Street'),('CSP','C Street Prepared','Street Prepared'),('CSR','C Street Race','Street Race'),('DM','D Modified','Modified'),('DP','D Prepared','Prepared'),('DS','D Street','Street'),('DSP','D Street Prepared','Street Prepared'),('DSR','D Street Race','Street Race'),('EM','E Modified','Modified'),('EP','E Prepared','Prepared'),('ES','E Street','Street'),('ESP','E Street Prepared','Street Prepared'),('ESR','E Street Race','Street Race'),('FM','F Modified','Modified'),('FP','F Prepared','Prepared'),('FS','F Street','Street'),('FSAE','Formula SAE','Formula SAE'),('FSP','F Street Prepared','Street Prepared'),('FSR','F Street Race','Street Race'),('GS','G Street','Street'),('GSR','G Street Race','Street Race'),('HCR',NULL,NULL),('HCS','',NULL),('HS','H Street','Street'),('HSR','H Street Race','Street Race'),('JA','Junior Kart A','Junior Kart'),('JB','Junior Kart B','Junior Kart'),('JC','Junior Kart C','Junior Kart'),('KM','Kart Modified','Modified'),('NR','Novice Race','Novice'),('NS','Novice Street','Novice'),('S1A',NULL,NULL),('S2A',NULL,NULL),('S2B',NULL,NULL),('S3',NULL,NULL),('SL',NULL,NULL),('SM','Street Modified','Street Modified'),('SMF','Street Modified FWD','Street Modified'),('SMST2','Street Modified Street Tire 2WD',NULL),('SMST4','Street Modified Street Tire 4WD',NULL),('SS','Super Street','Street'),('SSC','Solo Spec Coupe','Solo Spec Coupe'),('SSM','Super Street Modified','Street Modified'),('SSP','Super Street Prepared','Street Prepared'),('SSR','Super Street Race','Street Race'),('STC','Street Touring Compact','Street Touring'),('STF','Street Touring FWD','Street Touring'),('STH','Street Touring Hatch','Street Touring'),('STO','Street Touring Open','Street Touring'),('STP','Street Touring Pony','Street Touring'),('STR','Street Touring Roadster','Street Touring'),('STS','Street Touring Sport','Street Touring'),('STU','Street Touring Ultimate','Street Touring'),('STX','Street Touring Xtreme','Street Touring'),('XP','X Prepared','Prepared');
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clubs`
--

DROP TABLE IF EXISTS `clubs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `clubs` (
  `CLUB_ID` int(11) NOT NULL AUTO_INCREMENT,
  `CLUB_NAME` varchar(150) DEFAULT NULL,
  `CLUB_WEBPAGE` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`CLUB_ID`),
  UNIQUE KEY `CLUB_ID_UNIQUE` (`CLUB_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clubs`
--

LOCK TABLES `clubs` WRITE;
/*!40000 ALTER TABLE `clubs` DISABLE KEYS */;
INSERT INTO `clubs` VALUES (1,'CFR Solo','www.cfrsolo2.com'),(2,'Martin Sports Car Club','www.martinscc.org'),(3,'FAST','www.drivefast.org'),(4,'Gulf Coast Autocrossers','www.gulfcoastautocrossers.com'),(5,'Jax Solo',NULL),(6,'Coastal Empire',NULL);
/*!40000 ALTER TABLE `clubs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver_stats`
--

DROP TABLE IF EXISTS `driver_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `driver_stats` (
  `DS_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DS_NAME` varchar(100) NOT NULL,
  `DS_YEAR` int(11) NOT NULL,
  `DS_CLASS_NAME` varchar(10) NOT NULL,
  `DS_EVENTS` int(11) NOT NULL DEFAULT '0',
  `DS_RAW_WINS` int(11) NOT NULL DEFAULT '0',
  `DS_PAX_WINS` int(11) NOT NULL DEFAULT '0',
  `DS_CONES` int(11) NOT NULL DEFAULT '0',
  `DS_OFFCOURSES` int(11) NOT NULL DEFAULT '0',
  `DS_RUNS` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`DS_ID`),
  UNIQUE KEY `DS_ID_UNIQUE` (`DS_ID`),
  KEY `ClassToDriverStats_idx` (`DS_CLASS_NAME`),
  CONSTRAINT `ClassToDriverStats` FOREIGN KEY (`DS_CLASS_NAME`) REFERENCES `classes` (`class_name`)
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
 SET character_set_client = utf8mb4 ;
CREATE TABLE `events` (
  `EVENT_ID` int(11) NOT NULL AUTO_INCREMENT,
  `EVENT_CLUB_ID` int(11) NOT NULL,
  `EVENT_DATE` date NOT NULL,
  `EVENT_TYPE` varchar(20) NOT NULL,
  `EVENT_LOCATION_ID` int(11) NOT NULL,
  `EVENT_RAW_WINNER` varchar(100) DEFAULT NULL,
  `EVENT_PAX_WINNER` varchar(100) DEFAULT NULL,
  `EVENT_DRIVERS_COUNT` int(11) DEFAULT NULL,
  `EVENT_RUNS_PER_DRIVER` int(11) DEFAULT NULL,
  `EVENT_CONES_COUNT` int(11) DEFAULT NULL,
  `EVENT_OFFCOURSE_COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`EVENT_ID`),
  UNIQUE KEY `EVENT_ID_UNIQUE` (`EVENT_ID`),
  KEY `ClubToEvent_idx` (`EVENT_CLUB_ID`),
  KEY `LocationToEvent_idx` (`EVENT_LOCATION_ID`),
  CONSTRAINT `ClubToEvent` FOREIGN KEY (`EVENT_CLUB_ID`) REFERENCES `clubs` (`club_id`),
  CONSTRAINT `LocationToEvent` FOREIGN KEY (`EVENT_LOCATION_ID`) REFERENCES `locations` (`location_id`)
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
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `locations` (
  `LOCATION_ID` int(11) NOT NULL AUTO_INCREMENT,
  `LOCATION_NAME` varchar(100) DEFAULT NULL,
  `LOCATION_CITY` varchar(50) DEFAULT NULL,
  `LOCATION_STATE` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`LOCATION_ID`),
  UNIQUE KEY `LOCATION_ID_UNIQUE` (`LOCATION_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` VALUES (1,'OCCC','Orlando','FL'),(2,'Deland','Deland','FL'),(3,'Brooksville','Brooksville','FL'),(4,'Buckingham','Ft. Myers','FL'),(5,'Lake Tech','Tavares','FL'),(6,'Amelia','Fernandina Beach','FL'),(7,'Daytona','Daytona','FL'),(8,'FIRM','Starke','FL'),(9,'Gainesville','Gainesville','FL'),(10,'Valkaria','Valkaria','FL'),(11,'EFSC','Melbourne','FL'),(12,'Sebring','Sebring','FL'),(13,'Geneva','Geneva','FL'),(14,'Hutchinson Island','Savannah','GA'),(15,'Roebling Road','Bloomingdale','GA');
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pax`
--

DROP TABLE IF EXISTS `pax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `pax` (
  `PAX_ID` int(11) NOT NULL AUTO_INCREMENT,
  `PAX_YEAR` int(11) DEFAULT NULL,
  `PAX_VALUE` double DEFAULT NULL,
  `PAX_CLASS_NAME` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`PAX_ID`),
  UNIQUE KEY `PAX_ID_UNIQUE` (`PAX_ID`),
  KEY `ClassToPax_idx` (`PAX_CLASS_NAME`),
  CONSTRAINT `ClassToPax` FOREIGN KEY (`PAX_CLASS_NAME`) REFERENCES `classes` (`class_name`)
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pax`
--

LOCK TABLES `pax` WRITE;
/*!40000 ALTER TABLE `pax` DISABLE KEYS */;
INSERT INTO `pax` VALUES (1,2013,1,'AM'),(2,2014,1,'AM'),(3,2015,1,'AM'),(4,2016,1,'AM'),(5,2017,1,'AM'),(6,2013,0.847,'AS'),(7,2014,0.833,'AS'),(8,2015,0.829,'AS'),(9,2016,0.833,'AS'),(10,2017,0.819,'AS'),(11,2013,0.866,'ASP'),(12,2014,0.866,'ASP'),(13,2015,0.865,'ASP'),(14,2016,0.865,'ASP'),(15,2017,0.856,'ASP'),(16,2013,0,'ASR'),(17,2014,0.848,'ASR'),(18,2015,0,'ASR'),(19,2016,0,'ASR'),(20,2017,0,'ASR'),(21,2013,0.962,'BM'),(22,2014,0.965,'BM'),(23,2015,0.964,'BM'),(24,2016,0.966,'BM'),(25,2017,0.956,'BM'),(26,2013,0.878,'BP'),(27,2014,0.881,'BP'),(28,2015,0.881,'BP'),(29,2016,0.883,'BP'),(30,2017,0.869,'BP'),(31,2013,0.845,'BS'),(32,2014,0.831,'BS'),(33,2015,0.826,'BS'),(34,2016,0.826,'BS'),(35,2017,0.813,'BS'),(36,2013,0.858,'BSP'),(37,2014,0.863,'BSP'),(38,2015,0.863,'BSP'),(39,2016,0.863,'BSP'),(40,2017,0.853,'BSP'),(41,2013,0,'BSR'),(42,2014,0.845,'BSR'),(43,2015,0,'BSR'),(44,2016,0,'BSR'),(45,2017,0,'BSR'),(46,2013,0.83,'CAM'),(47,2014,0.83,'CAM'),(48,2015,0.83,'CAM'),(49,2016,0.839,'CAM'),(50,2017,0.823,'CAM'),(51,2013,0.83,'CAM-C'),(52,2014,0.83,'CAM-C'),(53,2015,0.83,'CAM-C'),(54,2016,0.839,'CAM-C'),(55,2017,0.823,'CAM-C'),(56,2013,0.836,'CAM-S'),(57,2014,0.836,'CAM-S'),(58,2015,0.836,'CAM-S'),(59,2016,0.848,'CAM-S'),(60,2017,0.838,'CAM-S'),(61,2013,0.825,'CAM-T'),(62,2014,0.825,'CAM-T'),(63,2015,0.825,'CAM-T'),(64,2016,0.834,'CAM-T'),(65,2017,0.817,'CAM-T'),(66,2013,0.91,'CM'),(67,2014,0.922,'CM'),(68,2015,0.919,'CM'),(69,2016,0.916,'CM'),(70,2017,0.901,'CM'),(71,2013,0.862,'CP'),(72,2014,0.864,'CP'),(73,2015,0.864,'CP'),(74,2016,0.864,'CP'),(75,2017,0.854,'CP'),(76,2013,0.834,'CS'),(77,2014,0.821,'CS'),(78,2015,0.814,'CS'),(79,2016,0.819,'CS'),(80,2017,0.81,'CS'),(81,2013,0.861,'CSP'),(82,2014,0.861,'CSP'),(83,2015,0.861,'CSP'),(84,2016,0.867,'CSP'),(85,2017,0.86,'CSP'),(86,2013,0,'CSR'),(87,2014,0.834,'CSR'),(88,2015,0,'CSR'),(89,2016,0,'CSR'),(90,2017,0,'CSR'),(91,2013,0.918,'DM'),(92,2014,0.92,'DM'),(93,2015,0.918,'DM'),(94,2016,0.919,'DM'),(95,2017,0.906,'DM'),(96,2013,0.874,'DP'),(97,2014,0.879,'DP'),(98,2015,0.879,'DP'),(99,2016,0.879,'DP'),(100,2017,0.865,'DP'),(101,2013,0.826,'DS'),(102,2014,0.815,'DS'),(103,2015,0.812,'DS'),(104,2016,0.811,'DS'),(105,2017,0.801,'DS'),(106,2013,0.855,'DSP'),(107,2014,0.855,'DSP'),(108,2015,0.854,'DSP'),(109,2016,0.855,'DSP'),(110,2017,0.842,'DSP'),(111,2013,0,'DSR'),(112,2014,0.825,'DSR'),(113,2015,0,'DSR'),(114,2016,0,'DSR'),(115,2017,0,'DSR'),(116,2013,0.928,'EM'),(117,2014,0.926,'EM'),(118,2015,0.922,'EM'),(119,2016,0.92,'EM'),(120,2017,0.905,'EM'),(121,2013,0.875,'EP'),(122,2014,0.876,'EP'),(123,2015,0.874,'EP'),(124,2016,0.871,'EP'),(125,2017,0.859,'EP'),(126,2013,0.828,'ES'),(127,2014,0.814,'ES'),(128,2015,0.808,'ES'),(129,2016,0.807,'ES'),(130,2017,0.794,'ES'),(131,2013,0.849,'ESP'),(132,2014,0.849,'ESP'),(133,2015,0.849,'ESP'),(134,2016,0.852,'ESP'),(135,2017,0.837,'ESP'),(136,2013,0,'ESR'),(137,2014,0.828,'ESR'),(138,2015,0,'ESR'),(139,2016,0,'ESR'),(140,2017,0,'ESR'),(141,2013,0.917,'FM'),(142,2014,0.924,'FM'),(143,2015,0.926,'FM'),(144,2016,0.926,'FM'),(145,2017,0.916,'FM'),(146,2013,0.877,'FP'),(147,2014,0.883,'FP'),(148,2015,0.88,'FP'),(149,2016,0.88,'FP'),(150,2017,0.873,'FP'),(151,2013,0.83,'FS'),(152,2014,0.818,'FS'),(153,2015,0.81,'FS'),(154,2016,0.814,'FS'),(155,2017,0.804,'FS'),(156,2013,0.989,'FSAE'),(157,2014,0.989,'FSAE'),(158,2015,0.984,'FSAE'),(159,2016,0.982,'FSAE'),(160,2017,0.966,'FSAE'),(161,2013,0.838,'FSP'),(162,2014,0.839,'FSP'),(163,2015,0.84,'FSP'),(164,2016,0.84,'FSP'),(165,2017,0.829,'FSP'),(166,2013,0,'FSR'),(167,2014,0.83,'FSR'),(168,2015,0,'FSR'),(169,2016,0,'FSR'),(170,2017,0,'FSR'),(171,2013,0.816,'GS'),(172,2014,0.806,'GS'),(173,2015,0.806,'GS'),(174,2016,0.806,'GS'),(175,2017,0.793,'GS'),(176,2013,0,'GSR'),(177,2014,0.812,'GSR'),(178,2015,0,'GSR'),(179,2016,0,'GSR'),(180,2017,0,'GSR'),(181,2013,0,'HCR'),(182,2014,0,'HCR'),(183,2015,0,'HCR'),(184,2016,0.838,'HCR'),(185,2017,0,'HCR'),(186,2013,0,'HCS'),(187,2014,0,'HCS'),(188,2015,0,'HCS'),(189,2016,0.817,'HCS'),(190,2017,0.809,'HCS'),(191,2013,0.804,'HS'),(192,2014,0.797,'HS'),(193,2015,0.796,'HS'),(194,2016,0.798,'HS'),(195,2017,0.786,'HS'),(196,2013,0,'HSR'),(197,2014,0.804,'HSR'),(198,2015,0,'HSR'),(199,2016,0,'HSR'),(200,2017,0,'HSR'),(201,2013,0.879,'JA'),(202,2014,0.88,'JA'),(203,2015,0.878,'JA'),(204,2016,0.878,'JA'),(205,2017,0.864,'JA'),(206,2013,0.842,'JB'),(207,2014,0.842,'JB'),(208,2015,0.842,'JB'),(209,2016,0.842,'JB'),(210,2017,0.834,'JB'),(211,2013,0.743,'JC'),(212,2014,0.741,'JC'),(213,2015,0.734,'JC'),(214,2016,0.734,'JC'),(215,2017,0.726,'JC'),(216,2013,0.955,'KM'),(217,2014,0.957,'KM'),(218,2015,0.955,'KM'),(219,2016,0.954,'KM'),(220,2017,0.939,'KM'),(221,2013,1,'NR'),(222,2014,1,'NR'),(223,2015,1,'NR'),(224,2016,1,'NR'),(225,2017,1,'NR'),(226,2013,1,'NS'),(227,2014,1,'NS'),(228,2015,1,'NS'),(229,2016,1,'NS'),(230,2017,1,'NS'),(231,2013,1,'S1A'),(232,2014,1,'S1A'),(233,2015,1,'S1A'),(234,2016,1,'S1A'),(235,2017,1,'S1A'),(236,2013,1,'S2A'),(237,2014,1,'S2A'),(238,2015,1,'S2A'),(239,2016,1,'S2A'),(240,2017,1,'S2A'),(241,2013,1,'S2B'),(242,2014,1,'S2B'),(243,2015,1,'S2B'),(244,2016,1,'S2B'),(245,2017,1,'S2B'),(246,2013,1,'S3'),(247,2014,1,'S3'),(248,2015,1,'S3'),(249,2016,1,'S3'),(250,2017,1,'S3'),(251,2013,1,'SL'),(252,2014,1,'SL'),(253,2015,1,'SL'),(254,2016,1,'SL'),(255,2017,1,'SL'),(256,2013,0.867,'SM'),(257,2014,0.87,'SM'),(258,2015,0.87,'SM'),(259,2016,0.87,'SM'),(260,2017,0.861,'SM'),(261,2013,0.852,'SMF'),(262,2014,0.851,'SMF'),(263,2015,0.853,'SMF'),(264,2016,0.861,'SMF'),(265,2017,0.848,'SMF'),(266,2013,0.858,'SMST2'),(267,2014,0.858,'SMST2'),(268,2015,0.858,'SMST2'),(269,2016,0.858,'SMST2'),(270,2017,0.858,'SMST2'),(271,2013,0.856,'SMST4'),(272,2014,0.856,'SMST4'),(273,2015,0.856,'SMST4'),(274,2016,0.856,'SMST4'),(275,2017,0.858,'SMST4'),(276,2013,0.859,'SS'),(277,2014,0.843,'SS'),(278,2015,0.835,'SS'),(279,2016,0.835,'SS'),(280,2017,0.826,'SS'),(281,2013,0.881,'SSM'),(282,2014,0.882,'SSM'),(283,2015,0.882,'SSM'),(284,2016,0.882,'SSM'),(285,2017,0.875,'SSM'),(286,2013,0.871,'SSP'),(287,2014,0.872,'SSP'),(288,2015,0.871,'SSP'),(289,2016,0.872,'SSP'),(290,2017,0.862,'SSP'),(291,2013,0,'SSR'),(292,2014,0.86,'SSR'),(293,2015,0.859,'SSR'),(294,2016,0.86,'SSR'),(295,2017,0.847,'SSR'),(296,2013,0.824,'STC'),(297,2014,0.824,'STC'),(298,2015,0,'STC'),(299,2016,0,'STC'),(300,2017,0,'STC'),(301,2013,0.795,'STF'),(302,2014,0.801,'STF'),(303,2015,0.801,'STF'),(304,2016,0.809,'STF'),(305,2017,0.8,'STF'),(306,2013,1,'STO'),(307,2014,1,'STO'),(308,2015,0.862,'STO'),(309,2016,0.862,'STO'),(310,2017,0.862,'STO'),(311,2013,0,'STP'),(312,2014,0,'STP'),(313,2015,0,'STP'),(314,2016,0.837,'STP'),(315,2017,0.82,'STP'),(316,2013,0.839,'STR'),(317,2014,0.838,'STR'),(318,2015,0.838,'STR'),(319,2016,0.841,'STR'),(320,2017,0.83,'STR'),(321,2013,0.829,'STS'),(322,2014,0.829,'STS'),(323,2015,0.828,'STS'),(324,2016,0.832,'STS'),(325,2017,0.818,'STS'),(326,2013,0.846,'STU'),(327,2014,0.846,'STU'),(328,2015,0.844,'STU'),(329,2016,0.845,'STU'),(330,2017,0.831,'STU'),(331,2013,0.827,'STX'),(332,2014,0.827,'STX'),(333,2015,0.831,'STX'),(334,2016,0.836,'STX'),(335,2017,0.822,'STX'),(336,2013,0.901,'XP'),(337,2014,0.906,'XP'),(338,2015,0.905,'XP'),(339,2016,0.907,'XP'),(340,2017,0.892,'XP');
/*!40000 ALTER TABLE `pax` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `runs`
--

DROP TABLE IF EXISTS `runs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `runs` (
  `RUN_ID` int(11) NOT NULL AUTO_INCREMENT,
  `RUN_EVENT_ID` int(11) NOT NULL,
  `RUN_CLASS_NAME` varchar(10) NOT NULL,
  `RUN_DRIVER` varchar(100) NOT NULL,
  `RUN_CAR_NAME` varchar(100) NOT NULL,
  `RUN_NUMBER` int(11) NOT NULL,
  `RUN_TIME` double DEFAULT NULL,
  `RUN_PAX_TIME` double NOT NULL,
  `RUN_CONES` int(11) NOT NULL,
  `RUN_OFFCOURSE` varchar(5) NOT NULL,
  PRIMARY KEY (`RUN_ID`),
  UNIQUE KEY `RUN_ID_UNIQUE` (`RUN_ID`),
  KEY `EventToRuns_idx` (`RUN_EVENT_ID`),
  KEY `ClassToRuns_idx` (`RUN_CLASS_NAME`),
  CONSTRAINT `ClassToRuns` FOREIGN KEY (`RUN_CLASS_NAME`) REFERENCES `classes` (`class_name`),
  CONSTRAINT `EventToRuns` FOREIGN KEY (`RUN_EVENT_ID`) REFERENCES `events` (`event_id`)
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

-- Dump completed on 2018-12-20 15:00:49
