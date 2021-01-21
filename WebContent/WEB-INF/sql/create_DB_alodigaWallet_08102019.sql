CREATE DATABASE  IF NOT EXISTS `alodigaWallet` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `alodigaWallet`;
-- MySQL dump 10.13  Distrib 5.5.62, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: alodigaWallet
-- ------------------------------------------------------
-- Server version	5.5.62-0ubuntu0.14.04.1

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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `countryId` bigint(3) NOT NULL,
  `stateId` bigint(10) DEFAULT NULL,
  `cityId` bigint(10) DEFAULT NULL,
  `countyId` bigint(10) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `zipCode` varchar(45) NOT NULL,
  `stateName` varchar(45) DEFAULT NULL,
  `countyName` varchar(45) DEFAULT NULL,
  `cityName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_address_city1` (`cityId`),
  KEY `fk_address_state1` (`stateId`),
  KEY `fk_address_county1` (`countyId`),
  KEY `fk_address_country1` (`countryId`),
  CONSTRAINT `fk_address_city1` FOREIGN KEY (`cityId`) REFERENCES `city` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_country1` FOREIGN KEY (`countryId`) REFERENCES `country` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_county1` FOREIGN KEY (`countyId`) REFERENCES `county` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_state1` FOREIGN KEY (`stateId`) REFERENCES `state` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (8,1,1,1,1,'la paz','1002','caracas','caracas','caracas'),(9,2,2,2,2,'DIRECCION','1023','APURE','APURE','APURE');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `balance_history`
--

DROP TABLE IF EXISTS `balance_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balance_history` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `userId` bigint(10) NOT NULL,
  `oldAmount` float(20,2) NOT NULL,
  `currentAmount` float(20,2) NOT NULL,
  `date` datetime NOT NULL,
  `productId` bigint(20) NOT NULL,
  `transactionId` bigint(20) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `adjusmentInfo` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `k_balance_history_transaction1` (`transactionId`),
  KEY `fk_balance_has_product` (`productId`),
  CONSTRAINT `fk_balance_has_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `k_balance_history_transaction1` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance_history`
--

LOCK TABLES `balance_history` WRITE;
/*!40000 ALTER TABLE `balance_history` DISABLE KEYS */;
INSERT INTO `balance_history` VALUES (1,379,4500.00,4200.00,'2019-09-23 15:00:00',3,15,1,NULL),(2,2,300000.00,295000.00,'2019-09-23 16:00:00',3,15,1,NULL),(3,379,4200.00,3700.00,'2019-09-23 14:35:39',3,39,1,NULL),(4,2,295000.00,294985.00,'2019-09-23 14:36:25',3,39,2,NULL),(5,379,3700.00,3200.00,'2019-09-24 09:08:09',3,40,3,NULL),(6,2,294985.00,294970.00,'2019-09-24 09:08:09',3,40,4,NULL),(7,379,3200.00,2700.00,'2019-09-24 10:40:34',3,46,5,NULL),(8,2,294970.00,294970.00,'2019-09-24 10:40:34',3,46,6,NULL),(9,379,2700.00,2200.00,'2019-09-24 10:41:11',3,47,7,NULL),(10,2,294970.00,294955.00,'2019-09-24 10:41:11',3,47,8,NULL),(25,379,2200.00,1700.00,'2019-09-24 16:05:15',3,55,9,NULL),(26,2,294955.00,294940.00,'2019-09-24 16:05:15',3,55,10,NULL),(37,379,1700.00,1000.00,'2019-09-27 10:46:01',3,76,25,NULL),(38,2,294940.00,294919.00,'2019-09-27 10:46:01',3,76,26,NULL),(39,379,1000.00,725.00,'2019-09-27 10:52:18',3,77,37,NULL),(40,2,294919.00,295169.00,'2019-09-27 10:52:18',3,77,38,NULL),(41,379,10000.00,10500.00,'2019-09-27 10:52:00',1,50,1,''),(42,379,15000.00,12800.00,'2019-09-27 11:00:00',4,51,1,''),(43,379,10500.00,10480.00,'2019-09-27 11:49:54',1,81,41,NULL),(44,379,12800.00,12840.00,'2019-09-27 11:49:55',4,81,42,NULL),(45,379,10480.00,10430.00,'2019-09-27 11:53:33',1,82,43,NULL),(46,379,12840.00,12940.00,'2019-09-27 11:53:33',4,82,44,NULL),(47,379,725.00,695.00,'2019-10-04 09:01:39',3,86,39,NULL),(48,2,295169.00,295168.09,'2019-10-04 09:01:39',3,86,40,NULL),(49,379,695.00,665.00,'2019-10-04 09:11:13',3,87,47,NULL),(50,2,295168.09,295167.19,'2019-10-04 09:11:13',3,87,48,NULL),(51,379,665.00,630.00,'2019-10-04 09:12:57',3,88,49,NULL),(52,2,295167.19,295166.12,'2019-10-04 09:12:57',3,88,50,NULL),(53,379,630.00,565.00,'2019-10-04 09:22:48',3,89,51,NULL),(54,2,295166.12,295206.12,'2019-10-04 09:22:48',3,89,52,NULL),(55,379,10430.00,10395.00,'2019-10-04 09:49:36',1,90,45,NULL),(56,379,12940.00,13010.00,'2019-10-04 09:49:36',4,90,46,NULL),(68,379,10000.00,9500.00,'2019-10-08 13:28:43',3,96,53,NULL),(69,2,295206.12,295191.12,'2019-10-08 13:28:43',3,96,54,NULL);
/*!40000 ALTER TABLE `balance_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `aba` varchar(30) NOT NULL,
  `countryId` bigint(30) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bank_has_enterprise_id` (`enterpriseId`),
  KEY `fk_bank_has_country_id` (`countryId`),
  CONSTRAINT `fk_bank_has_country_id` FOREIGN KEY (`countryId`) REFERENCES `country` (`id`),
  CONSTRAINT `fk_bank_has_enterprise_id` FOREIGN KEY (`enterpriseId`) REFERENCES `enterprise` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
INSERT INTO `bank` VALUES (1,'Banco Central Venezolano',1,'',1),(2,'Banesco',1,'',1),(3,'Bank of America',1,'',47);
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_has_product`
--

DROP TABLE IF EXISTS `bank_has_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_has_product` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(20) NOT NULL,
  `bankId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_has_product`
--

LOCK TABLES `bank_has_product` WRITE;
/*!40000 ALTER TABLE `bank_has_product` DISABLE KEYS */;
INSERT INTO `bank_has_product` VALUES (1,1,1),(2,2,1),(3,1,2);
/*!40000 ALTER TABLE `bank_has_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_operation`
--

DROP TABLE IF EXISTS `bank_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userSourceId` bigint(10) DEFAULT NULL,
  `productId` bigint(20) NOT NULL,
  `transactionId` bigint(3) NOT NULL,
  `bankOperationTypeId` bigint(3) NOT NULL,
  `bankId` bigint(3) NOT NULL,
  `bankOperationModeId` bigint(3) NOT NULL,
  `bankOperationNumber` varchar(40) DEFAULT NULL,
  `commisionId` bigint(20) DEFAULT NULL,
  `additional` varchar(500) DEFAULT NULL,
  `additional2` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bank_operation_has_product_id` (`productId`),
  KEY `fk_bank_operation_has_transaction_id` (`transactionId`),
  KEY `fk_bank_operation_has_commision_id` (`commisionId`),
  KEY `fk_bank_operation_bank1_idx` (`bankId`),
  KEY `fk_bank_operation_bank_operation_type1_idx` (`bankOperationTypeId`),
  KEY `fk_bank_operation_bank_operation_mode1_idx` (`bankOperationModeId`),
  CONSTRAINT `fk_bank_operation_has_commision_id` FOREIGN KEY (`commisionId`) REFERENCES `commission` (`id`),
  CONSTRAINT `fk_bank_operation_has_product_id` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_bank_operation_has_transaction_id` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`),
  CONSTRAINT `fk_bank_operation_bank1` FOREIGN KEY (`bankId`) REFERENCES `bank` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_bank_operation_bank_operation_type1` FOREIGN KEY (`bankOperationTypeId`) REFERENCES `bank_operation_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_bank_operation_bank_operation_mode1` FOREIGN KEY (`bankOperationModeId`) REFERENCES `bank_operation_mode` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_operation`
--

LOCK TABLES `bank_operation` WRITE;
/*!40000 ALTER TABLE `bank_operation` DISABLE KEYS */;
INSERT INTO `bank_operation` VALUES (1,379,3,95,1,1,1,'501878200085697411',10,NULL,NULL);
/*!40000 ALTER TABLE `bank_operation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_operation_mode`
--

DROP TABLE IF EXISTS `bank_operation_mode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_operation_mode` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_operation_mode`
--

LOCK TABLES `bank_operation_mode` WRITE;
/*!40000 ALTER TABLE `bank_operation_mode` DISABLE KEYS */;
INSERT INTO `bank_operation_mode` VALUES (1,'MANUAL'),(2,'AUTOMATIC');
/*!40000 ALTER TABLE `bank_operation_mode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_operation_type`
--

DROP TABLE IF EXISTS `bank_operation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_operation_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_operation_type`
--

LOCK TABLES `bank_operation_type` WRITE;
/*!40000 ALTER TABLE `bank_operation_type` DISABLE KEYS */;
INSERT INTO `bank_operation_type` VALUES (1,'WITHDRAWAL'),(2,'RECHARGE');
/*!40000 ALTER TABLE `bank_operation_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Moneda',1);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_data`
--

DROP TABLE IF EXISTS `category_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_data` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `categoryId` bigint(3) NOT NULL,
  `alias` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `languageId` bigint(3) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_category_data_category1` (`categoryId`),
  KEY `fk_category_data_language1` (`languageId`),
  CONSTRAINT `fk_category_data_category1` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_category_data_language1` FOREIGN KEY (`languageId`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_data`
--

LOCK TABLES `category_data` WRITE;
/*!40000 ALTER TABLE `category_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `stateId` bigint(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_city_state1` (`stateId`),
  CONSTRAINT `fk_city_state1` FOREIGN KEY (`stateId`) REFERENCES `state` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,1,'caracas'),(2,3,'ACHAGUAS');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `close`
--

DROP TABLE IF EXISTS `close`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `close` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `userComerceId` bigint(10) DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `totalAmount` float(20,2) DEFAULT NULL,
  `totalTax` float(20,2) NOT NULL,
  `totalToUser` float(20,2) NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `close`
--

LOCK TABLES `close` WRITE;
/*!40000 ALTER TABLE `close` DISABLE KEYS */;
/*!40000 ALTER TABLE `close` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `productId` bigint(20) NOT NULL,
  `transactionTypeId` bigint(20) NOT NULL,
  `beginningDate` datetime NOT NULL,
  `endingDate` datetime DEFAULT NULL,
  `isPercentCommision` tinyint(4) NOT NULL DEFAULT '1',
  `value` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_commision_has_transactionType` (`transactionTypeId`),
  KEY `fk_commision_has_product` (`productId`),
  CONSTRAINT `fk_commision_has_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_commision_has_transactionType` FOREIGN KEY (`transactionTypeId`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission`
--

LOCK TABLES `commission` WRITE;
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
INSERT INTO `commission` VALUES (1,3,2,'2019-09-20 17:54:46','2019-09-21 14:15:20',0,10),(2,3,2,'2019-09-20 17:54:46',NULL,1,3),(6,3,3,'2019-09-24 15:00:00',NULL,0,25),(7,3,3,'2019-09-24 01:00:00','2019-09-24 15:00:00',1,5),(8,1,4,'2019-09-26 07:00:00',NULL,1,2.5),(9,4,4,'2019-09-26 07:00:00',NULL,1,3),(10,3,5,'2019-10-01 10:00:00',NULL,1,5),(11,3,6,'2019-10-07 10:00:00',NULL,1,2.5);
/*!40000 ALTER TABLE `commission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_item`
--

DROP TABLE IF EXISTS `commission_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `commissionId` bigint(20) DEFAULT NULL,
  `amount` float(5,2) NOT NULL,
  `processedDate` datetime NOT NULL,
  `transactionId` bigint(20) NOT NULL,
  `isResidual` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_commission_item_transaction` (`transactionId`),
  KEY `fk_commission_item_commission` (`commissionId`),
  CONSTRAINT `fk_commission_item_commission` FOREIGN KEY (`commissionId`) REFERENCES `commission` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_commission_item_transaction_item` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_item`
--

LOCK TABLES `commission_item` WRITE;
/*!40000 ALTER TABLE `commission_item` DISABLE KEYS */;
INSERT INTO `commission_item` VALUES (1,2,0.60,'2019-10-07 10:41:43',16,0),(2,2,0.60,'2019-10-07 10:53:30',17,0),(3,2,0.90,'2019-10-07 10:53:32',18,0),(4,2,0.60,'2019-10-07 10:58:05',19,0),(5,2,9.00,'2019-10-07 11:00:15',20,0),(8,10,12.50,'2019-10-07 12:14:44',23,0),(9,2,0.90,'2019-10-07 14:35:15',24,0),(10,2,0.90,'2019-10-07 14:58:18',25,0),(11,2,0.90,'2019-10-07 15:04:34',26,0),(12,2,0.06,'2019-10-07 15:26:15',27,0),(13,2,0.06,'2019-10-07 15:28:00',28,0),(14,2,0.18,'2019-10-07 16:45:19',29,0),(15,2,0.60,'2019-10-07 16:50:48',30,0),(16,2,0.60,'2019-10-07 16:53:23',31,0),(17,2,0.30,'2019-10-07 16:55:20',32,0),(18,2,0.30,'2019-10-07 16:57:58',33,0),(19,2,0.30,'2019-10-07 17:00:05',34,0),(20,2,0.03,'2019-10-07 17:02:15',35,0),(21,2,0.30,'2019-10-07 17:05:47',36,0),(22,2,0.30,'2019-10-07 17:07:00',37,0),(23,2,0.03,'2019-10-08 08:41:50',38,0),(24,2,0.03,'2019-10-08 08:46:01',39,0),(25,2,0.03,'2019-10-08 08:48:11',40,0),(26,2,0.03,'2019-10-08 08:55:42',42,0),(27,2,0.03,'2019-10-08 09:39:46',43,0),(28,2,6.00,'2019-10-08 09:44:36',44,0),(29,2,0.12,'2019-10-08 10:02:40',45,0),(30,2,0.03,'2019-10-08 10:05:49',46,0),(31,10,5.00,'2019-10-08 12:03:41',95,0),(32,2,15.00,'2019-10-08 13:28:43',96,0);
/*!40000 ALTER TABLE `commission_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `shortName` varchar(5) DEFAULT NULL,
  `code` varchar(5) DEFAULT NULL,
  `alternativeName1` varchar(255) DEFAULT NULL,
  `alternativeName2` varchar(255) DEFAULT NULL,
  `alternativeName3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,'VENEZUELA','VE','58',NULL,NULL,'VENEZUELA'),(2,'AFGANISTÃN','AF','93',NULL,NULL,'AFGANISTAN'),(3,'ALBANIA','AL','355',NULL,NULL,'ALBANIA'),(4,'ALEMANIA','DE','49',NULL,NULL,'GERMANY'),(5,'ANDORRA','AD','376',NULL,NULL,'ANDORRA'),(6,'ANGOLA','AO','244',NULL,NULL,'ANGOLA'),(7,'ANGUILA','AI','1264',NULL,NULL,'ANGUILA'),(8,'ANTIGUA','AG','1268','','','ANTIGUA'),(9,'ANTILLAS HOLANDESAS','AN','599','BONAIRE','','NETHERLANDS ANTILLES'),(10,'ANTÃRTICA','','672',NULL,NULL,'ANTÃRTICA'),(11,'ARABIA SAUDITA','SA','966',NULL,NULL,'SAUDI ARABIA'),(12,'ARGELIA','DZ','21',NULL,NULL,'ARGELIA'),(13,'ARGENTINA','AR','54',NULL,NULL,'ARGENTINA'),(14,'ARMENIA','AM','374',NULL,NULL,'ARMENIA'),(15,'ARUBA','AW','297',NULL,NULL,'ARUBA'),(16,'AUSTRALIA','AU','61',NULL,NULL,'AUSTRALIA'),(17,'AUSTRIA','AT','43',NULL,NULL,'AUSTRIA'),(18,'AZERBAIJAN','','994',NULL,NULL,'AZERBAIJAN'),(19,'BAHAMAS','BS','1242',NULL,NULL,'BAHAMAS'),(20,'BAHRAIN','','973',NULL,NULL,'BAHRAIN'),(21,'BANGLADESH','BD','880','BANGLALINK BANGLA','','BANGLADESH'),(22,'BARBADOS','BB','1246',NULL,NULL,'BARBADOS'),(23,'BELICE','BZ','501',NULL,NULL,'BELIZE'),(24,'BERMUDA','','809',NULL,NULL,'BERMUDA'),(25,'BOLIVIA','BO','591',NULL,NULL,'BOLIVIA'),(26,'BRASIL','BR','55',NULL,NULL,'BRAZIL'),(27,'BULGARIA','BG','359',NULL,NULL,'BULGARIA'),(28,'BURKINA FASO','BF','226',NULL,NULL,'BURKINA FASO'),(29,'BÃ?LGICA','BE','32',NULL,NULL,'BELGIUM'),(30,'CAMBOYA','KH','855',NULL,NULL,'CAMBODIA'),(31,'CANADÃ','CA','1',NULL,NULL,'CANADÃ'),(32,'CHILE','CL','56',NULL,NULL,'CHILE'),(33,'CHINA','CN','86',NULL,NULL,'CHINA'),(34,'CHIPRE','CY','357','CYPRUS','','CYPRUS'),(35,'COLOMBIA','CO','57',NULL,NULL,'COLOMBIA'),(36,'COREA','','82',NULL,NULL,'SOUTH KOREA'),(37,'COSTA RICA','CR','506',NULL,NULL,'COSTA RICA'),(38,'CROACIA','HR','385',NULL,NULL,'CROATIA'),(39,'CUBA','CU','53',NULL,NULL,'CUBA'),(40,'DINAMARCA','DK','45',NULL,NULL,'DENMARK'),(41,'DOMINICA','DM','767',NULL,NULL,'DOMINICA'),(42,'ECUADOR','EC','593',NULL,NULL,'ECUADOR'),(43,'EGIPTO','EG','20',NULL,NULL,'EGYPT'),(44,'EL SALVADOR','SV','503',NULL,NULL,'EL SALVADOR'),(45,'EMIRATOS ÃRABES UNIDOS','AE','971',NULL,NULL,'UNITED ARAB EMIRATES'),(46,'ESPAÃ?A','ES','34',NULL,NULL,'SPAIN'),(47,'ESTADOS UNIDOS','US','1',NULL,NULL,'UNITED STATES'),(48,'ESTONIA','EE','372',NULL,NULL,'ESTONIA'),(49,'FINLANDIA','FI','358',NULL,NULL,'FINLAND'),(50,'FRANCIA','FR','33',NULL,NULL,'FRANCE'),(51,'GABÃ?N','GA','241',NULL,NULL,'GABON'),(52,'GRECIA','GR','30',NULL,NULL,'GREECE'),(53,'GUADALUPE','GP','590',NULL,NULL,'GUADALUPE'),(54,'GUATEMALA','GT','502',NULL,NULL,'GUATEMALA'),(55,'GUYANA','GY','592',NULL,NULL,'GUYANA'),(56,'GUYANA FRANCESA','','594',NULL,NULL,'FRENCH GUIANA'),(57,'HAITÃ','HT','509',NULL,NULL,'HAITÃ'),(58,'HOLANDA','','31',NULL,NULL,'NETHERLANDS'),(59,'HONDURAS','HN','504',NULL,NULL,'HONDURAS'),(60,'HONG KONG','HK','852',NULL,NULL,'HONG KONG'),(61,'HUNGRÃA','HU','36',NULL,NULL,'HUNGARY'),(62,'INDIA','IN','91',NULL,NULL,'INDIA'),(63,'INDIAS BRITÃNICAS','','246',NULL,NULL,'BRITISH INDIES'),(64,'INDONESIA','ID','62',NULL,NULL,'INDONESIA'),(65,'INGLATERRA','','44',NULL,NULL,'ENGLAND'),(66,'IRAK','IQ','964',NULL,NULL,'IRAK'),(67,'IRLANDA','IE','353',NULL,NULL,'IRELAND'),(68,'ISLANDIA','IS','354',NULL,NULL,'ICELAND'),(69,'ISLAS CAIMÃN','KY','1',NULL,NULL,'CAYMAN ISLANDS'),(70,'ISLAS PERIFÃ?RICAS DE LOS E.U.A','','1',NULL,NULL,'ISLAS PERIFÃ?RICAS DE LOS E.U.A'),(71,'ISLAS VÃRGENES','','1',NULL,NULL,'VIRGIN ISLANDS'),(72,'ISLAS VÃRGENES BRITÃNICAS','VG','1',NULL,NULL,'BRITISH VIRGIN ISLANDS'),(73,'ISRAEL','IL','972',NULL,NULL,'ISRAEL'),(74,'ITALIA','IT','39',NULL,NULL,'ITALY'),(75,'JAMAICA','JM','1',NULL,NULL,'JAMAICA'),(76,'JAPÃ?N','JP','81',NULL,NULL,'JAPAN'),(77,'JORDANIA','JO','962',NULL,NULL,'JORDAN'),(78,'KENYA','','254',NULL,NULL,'KENYA'),(79,'LETONIA','LV','371',NULL,NULL,'LETONIA'),(80,'LUXEMBURGO','LU','352',NULL,NULL,'LUXEMBURGO'),(81,'LÃBANO','LB','961',NULL,NULL,'LEBANON'),(82,'MALASIA','MY','60',NULL,NULL,'MALASIA'),(83,'MARRUECOS','MA','212',NULL,NULL,'MORROCO'),(84,'MICRONESIA','FM','691',NULL,NULL,'MICRONESIA'),(85,'MÃ?XICO','MX','52',NULL,NULL,'MEXICO'),(86,'MÃ?NACO','MC','377',NULL,NULL,'MONACO'),(87,'NICARAGUA','NI','505',NULL,NULL,'NICARAGUA'),(88,'NIGERIA','NG','234',NULL,NULL,'NIGERIA'),(89,'NORUEGA','NO','47',NULL,NULL,'NORWAY'),(90,'NUEVA ZELANDA','NZ','64',NULL,NULL,'NEW ZELAND'),(91,'OMÃN','OM','968',NULL,NULL,'OMAN'),(92,'OTRO','',NULL,NULL,NULL,'OTRO'),(93,'PAKISTÃN','PK','92',NULL,NULL,'PAKISTAN'),(94,'PANAMÃ','PA','507',NULL,NULL,'PANAMA'),(95,'PAPÃ?A/NUEVA GUINEA','PG','675','','','NEW GUINEA - PAPUA ISLAND'),(96,'PARAGUAY','PY','595',NULL,NULL,'PARAGUAY'),(97,'PERÃ?','PE','51',NULL,NULL,'PERU'),(98,'POLONIA','PL','48',NULL,NULL,'POLAND'),(99,'PORTUGAL','PT','351',NULL,NULL,'PORTUGAL'),(100,'PUERTO RICO','PR','1787',NULL,NULL,'PUERTO RICO'),(101,'REINO UNIDO','GB','44',NULL,NULL,'UNITED KINGDOM'),(102,'REPÃ?BLICA CHECA','CZ','420',NULL,NULL,'CZECH REPUBLIC'),(103,'REPÃ?BLICA DOMINICANA','DO','1',NULL,NULL,'DOMINICAN REPUBLIC'),(104,'RUMANIA','RO','40',NULL,NULL,'ROMANIA'),(105,'RUSIA','RU','7',NULL,NULL,'RUSSIA'),(106,'SAMOA','WS','685',NULL,NULL,'SAMOA'),(107,'SAMOA AMERICANA','AS','684',NULL,NULL,'AMERICAN SAMOA'),(108,'SAN VICENTE Y LAS GRANADINAS','VC','784','ST VINCENT GRENADINES',NULL,'ST VINCENT AND THE GRENADINES'),(109,'SINGAPUR','SG','65',NULL,NULL,'SINGAPORE'),(110,'SUDÃFRICA','ZA','27',NULL,NULL,'SOUTH AFRICA'),(111,'SUECIA','SE','46',NULL,NULL,'SWEDEN'),(112,'SUIZA','CH','41',NULL,NULL,'SWETZERLAND'),(113,'SURINAM','SR','597',NULL,NULL,'SURINAM'),(114,'TAILANDIA','TH','66',NULL,NULL,'THAILAND'),(115,'TAIWÃN','TW','886',NULL,NULL,'TAIWAN'),(116,'TRINIDAD Y TOBAGO','TT','868',NULL,NULL,'TRINIDAD AND TOBAGO'),(117,'TURQUÃA','TR','90',NULL,NULL,'TURKEY'),(118,'UCRANIA','UA','380',NULL,NULL,'UKRAINE'),(119,'UGANDA','UG','256',NULL,NULL,'UGANDA'),(120,'URUGUAY','UY','598',NULL,NULL,'URUGUAY'),(121,'VANUATU','VU','678',NULL,NULL,'VANUATU'),(122,'VATICANO','','396',NULL,NULL,'VATICAN CITY'),(123,'VIETNAM','VN','84',NULL,NULL,'VIETNAM'),(124,'ZIMBABWE','ZW','263',NULL,NULL,'ZIMBABWE'),(125,'CONGO','','242',NULL,NULL,'CONGO'),(126,'FILIPINAS','PH','63',NULL,NULL,'PHILIPPINES'),(127,'GHANA','GH','233',NULL,NULL,'GHANA'),(128,'GUINEA','GN','224','REPUBLICA DE GUINEA','GUINEA REPUBLIC','GUINEA'),(129,'GUINEA BISSAU','','245',NULL,NULL,'GUINEA BISSAU'),(130,'GUINEA ECUATORIAL','GQ','240',NULL,NULL,'EQUATORIAL GUINEA'),(131,'LAOS','LO','856',NULL,NULL,'LAOS'),(132,'LIBERIA','LR','231',NULL,NULL,'LIBERIA'),(133,'MADAGASCAR','MG','261',NULL,NULL,'MADAGASCAR'),(134,'MALI','ML','223',NULL,NULL,'MALI'),(135,'MOZAMBIQUE','MZ','258',NULL,NULL,'MOZAMBIQUE'),(136,'NIGER','NE','227',NULL,NULL,'NIGER'),(137,'PALESTINA','PS','930','','','PALESTINA'),(138,'REPUBLICA CENTROAFRICANA','CF','236',NULL,NULL,'CENTRAL AFRICAN REPUBLIC'),(139,'REPUBLICA DEMOCRATICA DEL CONGO','CD','243',NULL,NULL,'DEMOCRATIC REPUBLIC OF CONGO'),(140,'SENEGAL','SN','221',NULL,NULL,'SENEGAL'),(141,'SIRIA','SY','963',NULL,NULL,'SYRIA'),(142,'SUDAN','SD','249',NULL,NULL,'SUDAN'),(143,'TUNEZ','TN','216','TUNISIA',NULL,'TUNISIA'),(144,'YEMEN','YE','967',NULL,NULL,'YEMEN'),(145,'BURUNDI','BI','257',NULL,NULL,'BURUNDI'),(146,'CAMEROON','CM','237','','','CAMEROON'),(147,'FIJI','FJ','679','Digicel Fiji','','FIJI'),(148,'GRENADA','GD','1','','','GRENADA'),(149,'COSTA DE MARFIL','CI','225','','','IVORY COAST'),(150,'KAZAKHSTAN','','7',NULL,NULL,'KAZAKHSTAN'),(151,'MONTSERRAT','MS','664',NULL,NULL,'MONTSERRAT'),(152,'NEPAL','NP','977',NULL,NULL,'NEPAL'),(153,'RWANDA','','250',NULL,NULL,'RWANDA'),(154,'SOMALIA','SO','252',NULL,NULL,'SOMALIA'),(155,'SRI LANKA','LK','94',NULL,NULL,'SRI LANKA'),(156,'SAINT KITTS AND NEVIS','KN','1','ST KITTS AND NEVIS','','SAINT KITTS AND NEVIS'),(157,'SAINT LUCIA','LC','1','ST LUCIA','','SAINT LUCIA'),(158,'TANZANIA','TZ','255',NULL,NULL,'TANZANIA'),(159,'TURKS AND CAICOS','TC','1649','','','TURKS AND CAICOS'),(160,'ZAMBIA',' ','260','ZAMBIA','REPUBLICA  DE ZAMBIA','ZAMBIA'),(161,'BENIN',' ','229','BENIN','Republica de Benin','BENIN'),(162,'GAMBIA','GM','220','','','GAMBIA'),(163,'TOGO','TG','228','TOGO','TOGOLESA REPUBLIC','TOGO'),(164,'Swaziland','SZ','268','Swaziland','Reino de Swaziland','SWAZILAND'),(165,'CURACAO','CB','599','','','CURACAO'),(166,'KUWAIT','KW','965','KUWAIT','','KUWAIT'),(167,'MOLDOVA','MD','373','','','MOLDOVA'),(168,'NAURU','NR','674','','','NAURU'),(169,'SIERRA LEONE','SL','232','','','SIERRA LEONE'),(170,'TONGA','TO','676','','','TONGA');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `county`
--

DROP TABLE IF EXISTS `county`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `county` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `stateId` bigint(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `shortName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_county_state1` (`stateId`),
  CONSTRAINT `fk_county_state1` FOREIGN KEY (`stateId`) REFERENCES `state` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `county`
--

LOCK TABLES `county` WRITE;
/*!40000 ALTER TABLE `county` DISABLE KEYS */;
INSERT INTO `county` VALUES (1,1,'LIBERTADOR',NULL),(2,2,'SAN FERNANDO',NULL);
/*!40000 ALTER TABLE `county` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creditcard_type`
--

DROP TABLE IF EXISTS `creditcard_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditcard_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creditcard_type`
--

LOCK TABLES `creditcard_type` WRITE;
/*!40000 ALTER TABLE `creditcard_type` DISABLE KEYS */;
INSERT INTO `creditcard_type` VALUES (1,'VISA',1),(2,'MASTERCARD',1),(3,'MASTERCARD',1);
/*!40000 ALTER TABLE `creditcard_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `symbol` varchar(3) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
INSERT INTO `currency` VALUES (1,'DOLLAR','$'),(2,'BOLIVARES','BS');
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enterprise`
--

DROP TABLE IF EXISTS `enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enterprise` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `currencyId` bigint(3) NOT NULL,
  `countryId` bigint(3) NOT NULL,
  `name` varchar(45) NOT NULL,
  `url` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `atcNumber` varchar(45) NOT NULL,
  `address` varchar(255) NOT NULL,
  `invoiceAddress` varchar(255) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `infoEmail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_enterprise_currency1` (`currencyId`),
  KEY `fk_enterprise_country1` (`countryId`),
  CONSTRAINT `fk_enterprise_country1` FOREIGN KEY (`countryId`) REFERENCES `country` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_enterprise_currency1` FOREIGN KEY (`currencyId`) REFERENCES `currency` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enterprise`
--

LOCK TABLES `enterprise` WRITE;
/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
INSERT INTO `enterprise` VALUES (1,1,47,'AlodigaUSA','htttp://www.alodiga.com','customercare@alodiga.com','5842419340025','Miami Florida','Miami Florida',1,'customercare@alodiga.com'),(2,1,58,'Colombia','htttp://www.alodiga.com','customercare@alodiga.com','\'2\', \'1\', \'58\', \'Colombia\', \'htttp://www.alod','Miami Florida','Miami Florida',1,'customercare@alodiga.com');
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exchange_detail`
--

DROP TABLE IF EXISTS `exchange_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchange_detail` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `exchangeRateId` bigint(10) NOT NULL,
  `transactionId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_exchange_detail_product1_idx` (`productId`),
  KEY `fk_exchange_detail_exchange_rate1_idx` (`exchangeRateId`),
  KEY `fk_exchange_detail_transaction1_idx` (`transactionId`),
  CONSTRAINT `fk_exchange_detail_exchange_rate1` FOREIGN KEY (`exchangeRateId`) REFERENCES `exchange_rate` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_exchange_detail_product1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_exchange_detail_transaction1` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_detail`
--

LOCK TABLES `exchange_detail` WRITE;
/*!40000 ALTER TABLE `exchange_detail` DISABLE KEYS */;
INSERT INTO `exchange_detail` VALUES (1,1,2,9),(2,4,2,12),(3,4,2,4),(5,4,2,8),(6,4,2,13);
/*!40000 ALTER TABLE `exchange_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exchange_rate`
--

DROP TABLE IF EXISTS `exchange_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchange_rate` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `value` float NOT NULL,
  `beginningDate` datetime DEFAULT NULL,
  `endingDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_exchange_rate_product1_idx` (`productId`),
  CONSTRAINT `fk_exchange_rate_product1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_rate`
--

LOCK TABLES `exchange_rate` WRITE;
/*!40000 ALTER TABLE `exchange_rate` DISABLE KEYS */;
INSERT INTO `exchange_rate` VALUES (1,1,1,'2019-09-26 00:00:00',NULL),(2,4,0.5,'2019-09-26 00:00:00',NULL);
/*!40000 ALTER TABLE `exchange_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `key`
--

DROP TABLE IF EXISTS `key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `key` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `key`
--

LOCK TABLES `key` WRITE;
/*!40000 ALTER TABLE `key` DISABLE KEYS */;
/*!40000 ALTER TABLE `key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `iso` varchar(3) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
INSERT INTO `language` VALUES (1,'en','ENGLISH',1),(3,'es','SPANISH',1);
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parameter_type`
--

DROP TABLE IF EXISTS `parameter_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parameter_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parameter_type`
--

LOCK TABLES `parameter_type` WRITE;
/*!40000 ALTER TABLE `parameter_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `parameter_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_info`
--

DROP TABLE IF EXISTS `payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billingAddressId` bigint(10) NOT NULL,
  `paymentPatnerId` bigint(3) NOT NULL,
  `paymentTypeId` bigint(3) NOT NULL,
  `userId` bigint(10) DEFAULT NULL,
  `creditCardTypeId` bigint(3) DEFAULT NULL,
  `creditCardName` varchar(45) DEFAULT NULL COMMENT '	',
  `creditCardNumber` mediumblob,
  `creditCardCVV` varchar(255) DEFAULT NULL,
  `creditCardDate` date NOT NULL,
  `beginningDate` datetime NOT NULL,
  `endingDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payment_info_address1` (`billingAddressId`),
  KEY `fk_payment_info_payment_patner1` (`paymentPatnerId`),
  KEY `fk_payment_info_creditcard_type1` (`creditCardTypeId`),
  KEY `fk_payment_info_payment_type1` (`paymentTypeId`),
  CONSTRAINT `fk_payment_info_address1` FOREIGN KEY (`billingAddressId`) REFERENCES `address` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_payment_info_creditcard_type1` FOREIGN KEY (`creditCardTypeId`) REFERENCES `creditcard_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_payment_info_payment_patner1` FOREIGN KEY (`paymentPatnerId`) REFERENCES `payment_patner` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_payment_info_payment_type1` FOREIGN KEY (`paymentTypeId`) REFERENCES `payment_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES (7,8,2,1,379,1,'MOISES',NULL,'372','2014-05-01','2012-07-26 14:39:04',NULL),(9,9,3,3,3,3,'KERWIN',NULL,'380','2014-05-01','2012-07-26 14:39:04',NULL);
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_integration_type`
--

DROP TABLE IF EXISTS `payment_integration_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_integration_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_integration_type`
--

LOCK TABLES `payment_integration_type` WRITE;
/*!40000 ALTER TABLE `payment_integration_type` DISABLE KEYS */;
INSERT INTO `payment_integration_type` VALUES (1,'WEB SERVICE',1),(2,'PAYMENT GATEWAY',1),(3,'PAYMENT GATEWAY',1);
/*!40000 ALTER TABLE `payment_integration_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_patner`
--

DROP TABLE IF EXISTS `payment_patner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_patner` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `integrationTypeId` bigint(3) NOT NULL,
  `name` varchar(45) NOT NULL,
  `urlSubmit` varchar(255) DEFAULT NULL,
  `paymentUser` varchar(255) DEFAULT NULL,
  `encriptionKey` varchar(255) DEFAULT NULL,
  `urlResponse` varchar(255) DEFAULT NULL,
  `urlConfirmation` varchar(255) DEFAULT NULL,
  `testMode` tinyint(1) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payment_patner_integrationType1` (`integrationTypeId`),
  CONSTRAINT `fk_payment_patner_integrationType1` FOREIGN KEY (`integrationTypeId`) REFERENCES `payment_integration_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_patner`
--

LOCK TABLES `payment_patner` WRITE;
/*!40000 ALTER TABLE `payment_patner` DISABLE KEYS */;
INSERT INTO `payment_patner` VALUES (2,1,'Authorize.net',NULL,NULL,NULL,NULL,NULL,1,1),(3,2,'ALODIGA.COM',NULL,NULL,NULL,NULL,NULL,1,1),(4,3,'ALODIGA.COM',NULL,NULL,NULL,NULL,NULL,1,1);
/*!40000 ALTER TABLE `payment_patner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_type`
--

DROP TABLE IF EXISTS `payment_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_type`
--

LOCK TABLES `payment_type` WRITE;
/*!40000 ALTER TABLE `payment_type` DISABLE KEYS */;
INSERT INTO `payment_type` VALUES (1,'CASH',1),(2,'CREDIT_CARD',1),(3,'CREDIT_CARD',1);
/*!40000 ALTER TABLE `payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `period`
--

DROP TABLE IF EXISTS `period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `period` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `days` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `period`
--

LOCK TABLES `period` WRITE;
/*!40000 ALTER TABLE `period` DISABLE KEYS */;
/*!40000 ALTER TABLE `period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference`
--

DROP TABLE IF EXISTS `preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference`
--

LOCK TABLES `preference` WRITE;
/*!40000 ALTER TABLE `preference` DISABLE KEYS */;
INSERT INTO `preference` VALUES (1,'session',1,'Values relacionados con las sesion'),(2,'background',1,'Can set default and not default values'),(3,'transaction',1,'All related to transaction.'),(4,'commission',1,'Value relacionados con la session');
/*!40000 ALTER TABLE `preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference_control`
--

DROP TABLE IF EXISTS `preference_control`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference_control` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `preferenceId` bigint(3) DEFAULT NULL,
  `customerId` bigint(3) DEFAULT NULL,
  `userId` bigint(3) DEFAULT NULL,
  `paramValue` varchar(255) NOT NULL,
  `creationDate` datetime NOT NULL,
  `accessCounter` bigint(3) NOT NULL DEFAULT '0',
  `preferenceFieldId` bigint(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_preferenceId` (`preferenceId`),
  KEY `fk_preferenceFieldId` (`preferenceFieldId`),
  CONSTRAINT `fk_preferenceFieldId` FOREIGN KEY (`preferenceFieldId`) REFERENCES `preference_field` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_control`
--

LOCK TABLES `preference_control` WRITE;
/*!40000 ALTER TABLE `preference_control` DISABLE KEYS */;
/*!40000 ALTER TABLE `preference_control` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference_field`
--

DROP TABLE IF EXISTS `preference_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `preferenceId` bigint(20) NOT NULL,
  `enabled` tinyint(4) NOT NULL,
  `preferenceTypeId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_preference_field_preference` (`preferenceId`),
  KEY `fk_preference_field_preference_type` (`preferenceTypeId`),
  CONSTRAINT `fk_preference_field_preference` FOREIGN KEY (`preferenceId`) REFERENCES `preference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_preference_field_preference_type` FOREIGN KEY (`preferenceTypeId`) REFERENCES `preference_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_field`
--

LOCK TABLES `preference_field` WRITE;
/*!40000 ALTER TABLE `preference_field` DISABLE KEYS */;
INSERT INTO `preference_field` VALUES (1,'TIMEOUT_INACTIVE_SESSION',1,1,1),(4,'MAX_TRANSACTION_AMOUNT_LIMIT',3,1,1),(5,'MAX_TRANSACTION_NUMBER_BY_ACCOUNT',3,1,1),(6,'MAX_TRANSACTION_NUMBER_BY_CUSTOMER',3,1,1),(7,'MAX_WRONG_LOGIN_INTENT_NUMBER',1,1,1),(8,'PERIOD',1,1,1),(9,'DISABLED_TRANSACTION',3,1,1),(10,'MAX_TRANSACTION_AMOUNT_DAILY_LIMIT',3,1,1),(11,'MAX_PROMOTION_TRANSACTION_DAILY_LIMIT',3,1,1),(12,'DEFAULT_SMS_PROVIDER',2,1,1),(13,'BILL_PAYMENT_PREPAY_NATION_RECHARGE',3,1,1),(19,'MIN_VALUE_BALANCE_TRANSFER_TO',3,1,1);
/*!40000 ALTER TABLE `preference_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference_type`
--

DROP TABLE IF EXISTS `preference_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_type`
--

LOCK TABLES `preference_type` WRITE;
/*!40000 ALTER TABLE `preference_type` DISABLE KEYS */;
INSERT INTO `preference_type` VALUES (1,'INTEGER'),(2,'FLOAT'),(3,'STRING'),(4,'DATE'),(5,'PERIOD');
/*!40000 ALTER TABLE `preference_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference_value`
--

DROP TABLE IF EXISTS `preference_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference_value` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `value` varchar(45) NOT NULL,
  `beginningDate` datetime NOT NULL,
  `endingDate` datetime DEFAULT NULL,
  `preferenceFieldId` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_preference_value_preference_type` (`preferenceFieldId`),
  KEY `fk_general_option_enterprise1` (`enterpriseId`),
  CONSTRAINT `fk_general_option_enterprise1` FOREIGN KEY (`enterpriseId`) REFERENCES `enterprise` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_preference_value_preference_type` FOREIGN KEY (`preferenceFieldId`) REFERENCES `preference_field` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_value`
--

LOCK TABLES `preference_value` WRITE;
/*!40000 ALTER TABLE `preference_value` DISABLE KEYS */;
INSERT INTO `preference_value` VALUES (1,'1','2012-04-03 14:40:46','2012-04-03 15:17:48',4,1),(2,'300','2012-04-03 14:40:46','2012-04-04 09:57:02',5,1),(3,'-1','2012-04-03 14:40:46','2012-04-03 15:17:48',6,1),(4,'-1','2012-04-03 14:40:46','2012-04-03 15:10:37',7,1),(5,'200','2012-04-03 14:40:46','2012-04-04 09:48:02',1,1),(6,'-1','2012-04-03 14:41:43','2012-04-03 14:41:43',4,2),(7,'-1','2012-04-03 14:41:43','2012-04-03 16:13:54',5,2),(8,'-1','2012-04-03 14:41:43','2012-04-03 15:18:05',6,2),(9,'3','2012-04-03 14:41:43',NULL,7,2),(10,'100','2012-04-03 14:41:43',NULL,1,2),(12,'500','2012-04-03 15:10:37','2012-04-04 09:51:07',7,1),(13,'4','2012-04-03 15:10:37','2012-05-11 19:46:11',8,1),(14,'4','2012-04-03 15:10:52',NULL,8,2),(15,'5000','2012-04-03 15:17:48','2012-04-26 19:51:40',4,1),(16,'100','2012-04-03 15:17:48','2012-07-03 21:38:50',6,1),(17,'15','2012-04-03 15:18:05',NULL,6,2),(18,'300','2012-04-03 16:13:54',NULL,5,2),(19,'100','2012-04-04 09:48:02','2012-04-04 09:49:34',1,1),(20,'200','2012-04-04 09:49:34','2012-04-04 09:49:35',1,1),(21,'100','2012-04-04 09:49:35','2012-04-04 09:51:07',1,1),(22,'200','2012-04-04 09:51:07','2012-04-04 09:51:07',1,1),(23,'150','2012-04-04 09:51:07','2012-04-04 09:51:51',7,1),(24,'100','2012-04-04 09:51:07','2012-04-04 09:51:51',1,1),(25,'500','2012-04-04 09:51:51','2012-04-04 09:51:52',7,1),(26,'200','2012-04-04 09:51:51','2012-04-04 09:51:52',1,1),(27,'150','2012-04-04 09:51:52','2012-04-04 09:53:09',7,1),(28,'100','2012-04-04 09:51:52','2012-04-04 09:53:09',1,1),(29,'500','2012-04-04 09:53:09','2012-04-04 09:53:10',7,1),(30,'200','2012-04-04 09:53:09','2012-04-04 09:53:10',1,1),(31,'150','2012-04-04 09:53:10','2012-04-04 09:57:02',7,1),(32,'100','2012-04-04 09:53:10','2012-04-04 09:57:02',1,1),(33,'500','2012-04-04 09:57:02','2012-04-04 09:57:02',7,1),(34,'200','2012-04-04 09:57:02','2012-04-04 09:57:02',1,1),(35,'50','2012-04-04 09:57:02','2012-04-04 09:58:46',5,1),(36,'150','2012-04-04 09:57:02','2012-04-04 09:58:46',7,1),(37,'100','2012-04-04 09:57:02','2012-04-04 09:58:46',1,1),(38,'300','2012-04-04 09:58:46','2012-04-04 09:58:47',5,1),(39,'500','2012-04-04 09:58:46','2012-04-04 09:58:47',7,1),(40,'200','2012-04-04 09:58:46','2012-04-04 09:58:47',1,1),(41,'50','2012-04-04 09:58:47','2012-04-04 10:00:00',5,1),(42,'150','2012-04-04 09:58:47','2012-04-04 10:00:00',7,1),(43,'100','2012-04-04 09:58:47','2012-04-04 10:00:00',1,1),(44,'300','2012-04-04 10:00:00','2012-04-04 10:00:02',5,1),(45,'500','2012-04-04 10:00:00','2012-04-04 10:00:02',7,1),(46,'200','2012-04-04 10:00:00','2012-04-04 10:00:02',1,1),(47,'50','2012-04-04 10:00:02','2012-04-04 10:02:56',5,1),(48,'150','2012-04-04 10:00:02','2012-04-04 12:21:30',7,1),(49,'100','2012-04-04 10:00:02','2012-04-04 10:20:43',1,1),(50,'100','2012-04-04 10:02:56','2012-04-04 10:11:13',5,1),(51,'20','2012-04-04 10:11:13','2012-04-04 10:17:29',5,1),(52,'10','2012-04-04 10:17:29','2012-04-04 10:36:49',5,1),(53,'20','2012-04-04 10:20:43','2012-04-04 10:36:43',1,1),(54,'100','2012-04-04 10:36:43','2012-04-04 10:36:48',1,1),(55,'20','2012-04-04 10:36:48','2012-04-04 10:38:37',1,1),(56,'11','2012-04-04 10:36:49','2012-04-04 10:38:37',5,1),(57,'10','2012-04-04 10:38:37','2012-04-04 10:38:47',5,1),(58,'100','2012-04-04 10:38:37','2012-04-04 10:38:47',1,1),(59,'20','2012-04-04 10:38:47',NULL,1,1),(60,'11','2012-04-04 10:38:47','2012-04-04 11:44:46',5,1),(61,'1','2012-04-04 11:44:39','2012-04-04 11:53:00',9,1),(62,'15','2012-04-04 11:44:46','2012-05-09 20:27:36',5,1),(63,'0','2012-04-04 11:44:58','2012-04-04 11:57:12',9,2),(64,'0','2012-04-04 11:53:00','2012-04-04 11:53:15',9,1),(65,'0','2012-04-04 11:53:15','2012-04-26 16:37:57',9,1),(67,'150','2012-04-04 12:21:30',NULL,7,1),(68,'3','2012-04-04 12:21:30','2012-07-03 21:38:50',7,1),(69,'10000','2012-04-04 12:21:30','2013-03-28 18:51:09',10,1),(70,'15000','2012-04-26 19:51:40','2012-06-28 16:39:20',4,1),(71,'1','2012-04-26 16:37:57','2012-04-26 16:38:00',9,1),(72,'0','2012-04-26 16:38:00','2012-04-26 16:40:05',9,1),(73,'1','2012-04-26 16:40:05','2012-04-26 16:40:21',9,1),(74,'0','2012-04-26 16:40:21','2012-04-26 16:41:05',9,1),(75,'1','2012-04-26 16:41:05','2012-04-26 16:41:37',9,1),(76,'0','2012-04-26 16:41:37','2012-05-10 19:56:40',9,1),(77,'15','2012-04-26 16:41:37','2012-04-30 14:43:52',11,1),(78,'20','2012-04-30 14:43:52','2012-04-30 14:50:09',11,1),(79,'30','2012-04-30 14:50:09','2012-04-30 14:51:12',11,1),(80,'45','2012-04-30 14:51:12','2012-05-09 20:22:23',11,1),(81,'3','2012-05-09 20:22:23','2012-05-10 14:23:25',11,1),(82,'5','2012-05-09 20:27:36','2012-05-09 20:29:31',5,1),(83,'10','2012-05-09 20:29:31','2012-05-11 21:56:38',5,1),(84,'6','2012-05-10 14:23:25','2012-05-11 21:56:38',11,1),(85,'1','2012-05-10 19:56:40','2012-05-10 19:56:43',9,1),(86,'0','2012-05-10 19:56:43','2012-05-10 19:56:43',9,1),(87,'1','2012-05-10 19:56:43','2012-05-10 16:53:09',9,1),(88,'0','2012-05-10 16:53:09','2012-05-10 16:53:11',9,1),(89,'1','2012-05-10 16:53:11','2012-05-10 16:53:12',9,1),(90,'0','2012-05-10 16:53:12','2012-05-10 21:16:57',9,1),(91,'1','2012-05-10 21:16:57','2012-10-31 11:53:17',9,1),(92,'2','2012-05-11 19:46:11','2012-05-16 19:03:43',8,1),(93,'2','2012-05-11 21:56:38','2012-05-11 21:56:41',5,1),(94,'2','2012-05-11 21:56:38','2012-05-21 14:21:54',11,1),(95,'3','2012-05-11 21:56:41','2012-05-11 22:08:42',5,1),(96,'5','2012-05-11 22:08:42','2012-05-21 14:21:54',5,1),(97,'4','2012-05-16 19:03:43','2012-05-21 14:21:54',8,1),(98,'2','2012-05-21 14:21:54','2012-05-21 15:46:55',5,1),(99,'2','2012-05-21 14:21:54','2014-04-01 15:38:06',8,1),(100,'1','2012-05-21 14:21:54','2012-05-21 15:46:55',11,1),(101,'100','2012-05-21 15:46:55','2012-07-03 21:38:50',5,1),(102,'10','2012-05-21 15:46:55','2012-10-24 15:51:52',11,1),(103,'100','2012-06-28 16:39:20','2012-06-28 17:57:03',4,1),(104,'5000','2012-06-28 17:57:03',NULL,4,1),(105,'1000','2012-07-03 21:38:50','2012-10-04 11:21:06',5,1),(106,'1000','2012-07-03 21:38:50','2012-10-26 13:50:01',6,1),(107,'5','2012-07-03 21:38:50',NULL,7,1),(108,'2000','2012-10-04 11:21:06','2012-10-26 13:50:01',5,1),(109,'100','2012-10-24 15:51:52','2012-10-26 13:50:01',11,1),(110,'10000','2012-10-26 13:50:01','2013-03-28 18:51:30',5,1),(111,'25','2012-10-26 13:50:01',NULL,6,1),(112,'500','2012-10-26 13:50:01','2013-04-05 16:31:12',11,1),(113,'0','2012-10-31 11:53:17','2012-10-31 11:55:14',9,1),(114,'1','2012-10-31 11:55:14','2013-02-25 16:27:18',9,1),(115,'0','2013-02-25 16:27:18','2013-02-25 16:27:20',9,1),(116,'1','2013-02-25 16:27:20',NULL,9,1),(117,'4','2013-04-03 14:40:46','2013-03-21 05:52:05',12,1),(118,'4','2013-04-03 14:40:46',NULL,12,2),(119,'5','2013-03-21 05:52:05','2013-03-21 05:52:56',12,1),(120,'4','2013-03-21 05:52:56','2013-07-06 11:11:36',12,1),(121,'15000','2013-03-28 18:51:09',NULL,10,1),(122,'25','2013-03-28 18:51:30',NULL,5,1),(123,'10','2013-04-05 16:31:12','2013-10-23 19:36:01',11,1),(124,'5','2013-07-06 11:11:36','2013-07-06 17:34:58',12,1),(125,'4','2013-07-06 17:34:58',NULL,12,1),(126,'1.5','2013-07-11 09:00:00',NULL,13,1),(127,'15000','2013-10-23 19:36:01',NULL,11,1),(128,'1','2014-04-01 15:38:06',NULL,8,1);
/*!40000 ALTER TABLE `preference_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `enterpriseId` bigint(3) NOT NULL,
  `categoryId` bigint(3) NOT NULL,
  `productIntegrationTypeId` bigint(3) NOT NULL,
  `name` varchar(45) NOT NULL,
  `taxInclude` tinyint(1) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `referenceCode` varchar(45) NOT NULL,
  `ratesUrl` varchar(255) DEFAULT NULL,
  `accessNumberUrl` varchar(255) DEFAULT NULL,
  `isFree` tinyint(1) NOT NULL DEFAULT '1',
  `isAlocashProduct` tinyint(1) NOT NULL,
  `symbol` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_service1` (`categoryId`),
  KEY `fk_product_integration_type1` (`productIntegrationTypeId`),
  KEY `fk_product_enterprise1` (`enterpriseId`),
  CONSTRAINT `fk_product_enterprise1` FOREIGN KEY (`enterpriseId`) REFERENCES `enterprise` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_integrationType1` FOREIGN KEY (`productIntegrationTypeId`) REFERENCES `product_integration_type` (`id`),
  CONSTRAINT `fk_product_service1` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,1,1,1,'Alocoin',0,1,'1001','htttp://www.alodiga.com','584241934005',0,1,''),(2,1,1,1,'Saldo Alodiga',0,1,'1001','htttp://www.alodiga.com','584241934005',0,1,''),(3,1,1,1,'Tarjeta Prepagada',0,0,'1001','htttp://www.alodiga.com','584241934005',0,1,''),(4,1,1,1,'HealtCoin',0,1,'1002','htttp://www.alodiga.com','584241934005',0,1,''),(5,1,1,1,'Dolar',0,1,'1003','htttp://www.alodiga.com','584241934005',0,1,''),(6,1,1,1,'Bolívar',0,1,'1004','htttp://www.alodiga.com','584241934005',0,1,'');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_data`
--

DROP TABLE IF EXISTS `product_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_data` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `alias` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `languageId` bigint(3) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_product_data_product1` (`productId`),
  KEY `fk_product_data_language1` (`languageId`),
  CONSTRAINT `fk_product_data_language1` FOREIGN KEY (`languageId`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_product_data_product1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_data`
--

LOCK TABLES `product_data` WRITE;
/*!40000 ALTER TABLE `product_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_has_provider`
--

DROP TABLE IF EXISTS `product_has_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_has_provider` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `providerId` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_has_provider_product1` (`productId`),
  KEY `fk_product_has_provider_provider` (`providerId`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_has_provider`
--

LOCK TABLES `product_has_provider` WRITE;
/*!40000 ALTER TABLE `product_has_provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_has_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_integration_type`
--

DROP TABLE IF EXISTS `product_integration_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_integration_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_integration_type`
--

LOCK TABLES `product_integration_type` WRITE;
/*!40000 ALTER TABLE `product_integration_type` DISABLE KEYS */;
INSERT INTO `product_integration_type` VALUES (1,'Moneda Local');
/*!40000 ALTER TABLE `product_integration_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion`
--

DROP TABLE IF EXISTS `promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `transactionTypeId` bigint(3) DEFAULT NULL,
  `periodId` bigint(3) DEFAULT NULL,
  `name` varchar(2000) NOT NULL DEFAULT ' ',
  `promotionType` enum('INITIAL_PURCHASE','ACCOUNT_CREATION','GOAL_ACCOMPLISHMENT') NOT NULL,
  `promotionalAction` enum('BONUS','DISCOUNT') NOT NULL,
  `beginningDate` datetime NOT NULL,
  `endingDate` datetime NOT NULL,
  `isPercentage` tinyint(1) NOT NULL DEFAULT '0',
  `goalAmount` float DEFAULT NULL,
  `promotionalAmount` float NOT NULL DEFAULT '0',
  `promotionValidityDays` int(11) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_promotion_product` (`productId`),
  KEY `fk_promotion_transaction_type` (`transactionTypeId`),
  KEY `fk_promotion_period` (`periodId`),
  CONSTRAINT `fk_promotion_period` FOREIGN KEY (`periodId`) REFERENCES `period` (`id`),
  CONSTRAINT `fk_promotion_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_promotion_transaction_type` FOREIGN KEY (`transactionTypeId`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion`
--

LOCK TABLES `promotion` WRITE;
/*!40000 ALTER TABLE `promotion` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion_data`
--

DROP TABLE IF EXISTS `promotion_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotionId` bigint(20) NOT NULL,
  `languageId` bigint(20) NOT NULL,
  `smsText` varchar(160) NOT NULL,
  `mailText` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `new_fk_constraint` (`promotionId`),
  KEY `new_fk_constraint_language` (`languageId`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`promotionId`) REFERENCES `promotion` (`id`),
  CONSTRAINT `new_fk_constraint_language` FOREIGN KEY (`languageId`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_data`
--

LOCK TABLES `promotion_data` WRITE;
/*!40000 ALTER TABLE `promotion_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion_item`
--

DROP TABLE IF EXISTS `promotion_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_item` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `promotionId` bigint(10) DEFAULT NULL,
  `transactionId` bigint(10) DEFAULT NULL,
  `promotionalAmount` float NOT NULL,
  `promotionApplicationDate` datetime NOT NULL,
  `comments` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_promotion_item_promotion` (`promotionId`),
  KEY `fk_promotion_item_transacction_item1` (`transactionId`),
  CONSTRAINT `fk_promotion_item_promotion` FOREIGN KEY (`promotionId`) REFERENCES `promotion` (`id`),
  CONSTRAINT `fk_promotion_item_transacction_item1` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_item`
--

LOCK TABLES `promotion_item` WRITE;
/*!40000 ALTER TABLE `promotion_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion_notification`
--

DROP TABLE IF EXISTS `promotion_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_notification` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `languageId` bigint(3) NOT NULL,
  `promotionType` enum('INITIAL_PURCHASE','ACCOUNT_CREATION','GOAL_ACCOMPLISHMENT') NOT NULL,
  `ackMailFrom` varchar(50) NOT NULL,
  `ackMailSubject` varchar(100) NOT NULL,
  `ackMailHtml` text NOT NULL,
  `ackSmsFrom` varchar(20) NOT NULL,
  `ackSmsText` varchar(140) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_promotion_notification_language` (`languageId`),
  CONSTRAINT `fk_promotion_notification_language` FOREIGN KEY (`languageId`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_notification`
--

LOCK TABLES `promotion_notification` WRITE;
/*!40000 ALTER TABLE `promotion_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider`
--

DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `url` varchar(255) NOT NULL,
  `isSMSProvider` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `aditionalPercent` float DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider`
--

LOCK TABLES `provider` WRITE;
/*!40000 ALTER TABLE `provider` DISABLE KEYS */;
INSERT INTO `provider` VALUES (1,'transferto','www',0,1,20);
/*!40000 ALTER TABLE `provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` text NOT NULL,
  `reportTypeId` bigint(3) NOT NULL,
  `query` text,
  `webServiceUrl` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_reportTypeId_ReportType` (`reportTypeId`),
  CONSTRAINT `fk_reportTypeId_ReportType` FOREIGN KEY (`reportTypeId`) REFERENCES `report_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_parameter`
--

DROP TABLE IF EXISTS `report_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_parameter` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `parameterTypeId` bigint(3) NOT NULL,
  `reportId` bigint(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `required` tinyint(1) NOT NULL,
  `indexOrder` int(11) NOT NULL,
  `defaultValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_report_parameter_parameter_type1` (`parameterTypeId`),
  KEY `fk_report_parameter_report1` (`reportId`),
  CONSTRAINT `fk_report_parameter_parameter_type1` FOREIGN KEY (`parameterTypeId`) REFERENCES `parameter_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_report_parameter_report1` FOREIGN KEY (`reportId`) REFERENCES `report` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_parameter`
--

LOCK TABLES `report_parameter` WRITE;
/*!40000 ALTER TABLE `report_parameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_type`
--

DROP TABLE IF EXISTS `report_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_type`
--

LOCK TABLES `report_type` WRITE;
/*!40000 ALTER TABLE `report_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms`
--

DROP TABLE IF EXISTS `sms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` bigint(10) DEFAULT NULL,
  `integratorName` varchar(45) DEFAULT NULL,
  `sender` varchar(45) DEFAULT NULL,
  `destination` varchar(45) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `creationDate` datetime NOT NULL,
  `additional` varchar(4000) DEFAULT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms`
--

LOCK TABLES `sms` WRITE;
/*!40000 ALTER TABLE `sms` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `state` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `countryId` bigint(3) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_state_country1` (`countryId`),
  CONSTRAINT `fk_state_country1` FOREIGN KEY (`countryId`) REFERENCES `country` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES (1,1,'dtto. capital'),(2,2,'AMAZONAS'),(3,3,'APURE');
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userSourceId` bigint(10) DEFAULT NULL,
  `userDestinationId` bigint(20) DEFAULT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `paymentInfoId` bigint(20) DEFAULT NULL,
  `transactionTypeId` bigint(3) NOT NULL,
  `transactionSourceId` bigint(3) NOT NULL,
  `creationDate` datetime NOT NULL,
  `amount` float(20,2) NOT NULL,
  `transactionStatus` varchar(45) NOT NULL,
  `totalTax` float(20,2) DEFAULT NULL,
  `totalAmount` float(20,2) DEFAULT NULL,
  `promotionAmount` float(20,2) DEFAULT '0.00',
  `totalAlopointsUsed` float(20,2) DEFAULT NULL,
  `topUpDescription` varchar(1000) DEFAULT NULL,
  `billPaymentDescription` varchar(1000) DEFAULT NULL,
  `externalId` varchar(255) DEFAULT NULL,
  `additional` varchar(500) DEFAULT NULL,
  `additional2` varchar(500) DEFAULT NULL,
  `closeId` bigint(20) DEFAULT NULL,
  `concept` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_transaction_payment_info1` (`paymentInfoId`),
  KEY `fk_transaction_transaction_source1` (`transactionSourceId`),
  KEY `fk_transaction_transaction_type1` (`transactionTypeId`),
  KEY `ind_transaction_status_1` (`transactionStatus`),
  KEY `ind_transaction_creationDate_1` (`creationDate`),
  KEY `fk_transaction_has_product` (`productId`),
  KEY `fk_transaction_has_close_id` (`closeId`),
  CONSTRAINT `fk_transaction_has_close_id` FOREIGN KEY (`closeId`) REFERENCES `close` (`id`),
  CONSTRAINT `fk_transaction_has_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_transaction_payment_info1` FOREIGN KEY (`paymentInfoId`) REFERENCES `payment_info` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_transaction_transaction_source1` FOREIGN KEY (`transactionSourceId`) REFERENCES `transaction_source` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_transaction_transaction_type1` FOREIGN KEY (`transactionTypeId`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (5,379,380,3,3,1,1,'2012-07-26 14:39:06',0.00,'20.00',0.00,0.00,20.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(12,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'150.00',0.00,0.00,150.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(13,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'200.00',0.00,0.00,150.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(14,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'200.00',0.00,0.00,150.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(15,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'500.00',0.00,0.00,150.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(16,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'800.00',0.00,0.00,350.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(17,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'350.00',0.00,0.00,850.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(18,379,380,3,3,1,1,'2019-09-20 14:39:06',0.00,'200.00',0.00,0.00,250.00,0.00,'0.00','null',NULL,NULL,NULL,NULL,NULL),(19,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(20,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(21,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(22,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(23,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(24,379,2,3,NULL,2,2,'2019-09-20 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(26,379,2,3,NULL,2,2,'2019-09-23 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(27,379,2,3,NULL,2,2,'2019-09-23 00:00:00',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(28,379,2,3,NULL,2,2,'2019-09-23 10:32:47',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(29,379,2,3,NULL,2,2,'2019-09-23 10:47:09',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(30,379,2,3,NULL,2,2,'2019-09-23 10:52:40',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(32,379,2,3,NULL,2,2,'2019-09-23 11:54:36',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(34,379,2,3,NULL,2,2,'2019-09-23 12:01:54',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(35,379,2,3,NULL,2,2,'2019-09-23 12:09:42',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(36,379,2,3,NULL,2,2,'2019-09-23 13:40:45',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(38,379,2,3,NULL,2,2,'2019-09-23 14:05:21',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(39,379,2,3,NULL,2,2,'2019-09-23 14:35:39',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(40,379,2,3,NULL,2,2,'2019-09-24 09:07:53',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(46,379,2,3,NULL,2,2,'2019-09-24 10:40:34',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(47,379,2,3,NULL,2,2,'2019-09-24 10:41:11',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(48,379,2,3,NULL,2,2,'2019-09-24 10:42:22',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(49,379,2,3,NULL,2,2,'2019-09-24 10:44:44',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(50,379,2,3,NULL,2,2,'2019-09-24 10:46:06',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(51,379,2,3,NULL,2,2,'2019-09-24 10:48:04',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(52,379,2,3,NULL,2,2,'2019-09-24 13:53:40',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(53,379,2,3,NULL,2,2,'2019-09-24 15:27:34',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(54,379,2,3,NULL,2,2,'2019-09-24 16:01:07',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(55,379,2,3,NULL,2,2,'2019-09-24 16:05:15',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(56,379,2,3,NULL,3,2,'2019-09-25 10:00:05',0.00,'250.00',0.00,0.00,250.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(57,379,2,3,NULL,3,2,'2019-09-25 10:00:47',0.00,'250.00',0.00,0.00,250.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(58,379,2,3,NULL,3,2,'2019-09-25 10:04:02',0.00,'250.00',0.00,0.00,250.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(59,379,2,3,NULL,3,2,'2019-09-25 10:10:26',0.00,'250.00',0.00,0.00,250.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(60,379,2,3,NULL,3,2,'2019-09-25 10:16:06',0.00,'200.00',0.00,0.00,200.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(61,379,2,3,NULL,3,2,'2019-09-25 10:34:17',0.00,'250.00',0.00,0.00,250.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(62,379,2,3,NULL,3,2,'2019-09-25 10:40:12',0.00,'150.00',0.00,0.00,150.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(63,379,2,3,NULL,2,2,'2019-09-26 09:29:43',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(64,379,2,3,NULL,2,2,'2019-09-26 09:32:10',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(65,379,2,3,NULL,2,2,'2019-09-26 09:32:49',0.00,'500.00',0.00,0.00,500.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(66,379,NULL,1,NULL,4,2,'2019-09-26 14:39:11',0.00,'15.00',0.00,NULL,15.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(67,379,NULL,4,NULL,4,2,'2019-09-26 14:40:22',0.00,'30.00',0.00,NULL,30.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(75,379,NULL,1,NULL,4,2,'2019-09-27 08:48:15',0.00,'40.00',0.00,NULL,40.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(76,379,2,3,NULL,2,2,'2019-09-27 10:46:01',0.00,'700.00',0.00,NULL,700.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(77,379,2,3,NULL,3,2,'2019-09-27 10:52:18',0.00,'250.00',0.00,NULL,250.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(78,379,NULL,1,NULL,4,2,'2019-09-27 10:53:59',0.00,'20.00',0.00,NULL,20.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(79,379,NULL,1,NULL,4,2,'2019-09-27 11:45:37',0.00,'20.00',0.00,NULL,20.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(81,379,NULL,1,NULL,4,2,'2019-09-27 11:49:46',0.00,'20.00',0.00,NULL,20.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(82,379,NULL,1,NULL,4,2,'2019-09-27 11:53:33',0.00,'50.00',0.00,NULL,50.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(84,379,NULL,3,NULL,5,2,'2019-10-01 11:41:39',0.00,'25.00',0.00,NULL,25.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(85,379,NULL,3,NULL,5,2,'2019-10-01 11:53:10',0.00,'350.00',0.00,NULL,350.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(86,379,2,3,NULL,2,2,'2019-10-04 00:00:00',0.00,'30.00',0.00,0.00,30.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(87,379,2,3,NULL,2,2,'2019-10-04 00:00:00',0.00,'30.00',0.00,0.00,30.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(88,379,2,3,NULL,2,2,'2019-10-04 09:12:57',0.00,'35.00',0.00,0.00,35.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(89,379,2,3,NULL,3,2,'2019-10-04 09:22:48',0.00,'40.00',0.00,0.00,40.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(90,379,NULL,1,NULL,4,2,'2019-10-04 09:49:36',0.00,'35.00',0.00,0.00,35.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(91,379,NULL,3,NULL,5,2,'2019-10-04 10:05:02',0.00,'400.00',0.00,0.00,400.00,0.00,'0.00',NULL,NULL,NULL,NULL,NULL,NULL),(93,379,NULL,3,NULL,5,2,'2019-10-08 10:07:16',0.00,'250.00',0.00,NULL,250.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(94,379,NULL,3,NULL,5,2,'2019-10-08 10:41:23',0.00,'120.00',0.00,NULL,120.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(95,379,NULL,3,NULL,5,2,'2019-10-08 12:03:41',100.00,'IN_PROCESS',NULL,100.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RETIRO MANUAL'),(96,379,2,3,NULL,2,2,'2019-10-08 13:28:43',500.00,'COMPLETED',NULL,500.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Payment Shop');
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_source`
--

DROP TABLE IF EXISTS `transaction_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_source` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COMMENT='Contiene el origen de la transaccion, si es por IVR, pagina ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_source`
--

LOCK TABLES `transaction_source` WRITE;
/*!40000 ALTER TABLE `transaction_source` DISABLE KEYS */;
INSERT INTO `transaction_source` VALUES (1,'ALODIGA'),(2,'alo'),(3,'alo'),(4,'CUSTOMER_WEB'),(5,'ACCOUNT_WEB'),(6,'ACCOUNT_WEB');
/*!40000 ALTER TABLE `transaction_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_type`
--

DROP TABLE IF EXISTS `transaction_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_type`
--

LOCK TABLES `transaction_type` WRITE;
/*!40000 ALTER TABLE `transaction_type` DISABLE KEYS */;
INSERT INTO `transaction_type` VALUES (1,'PRODUCT_RECHARGE'),(2,'PRODUCT_PAYMENT'),(3,'PRODUCT_TRANSFER'),(4,'PRODUCT_EXCHANGE'),(5,'WITHDRAWALS_MANUAL'),(6,'MANUAL_RECHARGE');
/*!40000 ALTER TABLE `transaction_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_has_bank`
--

DROP TABLE IF EXISTS `user_has_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_has_bank` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `userSourceId` bigint(20) NOT NULL,
  `bankId` bigint(20) NOT NULL,
  `accountNumber` varchar(25) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_has_bank_has_bank_id` (`bankId`),
  CONSTRAINT `fk_user_has_bank_has_bank_id` FOREIGN KEY (`bankId`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_has_bank`
--

LOCK TABLES `user_has_bank` WRITE;
/*!40000 ALTER TABLE `user_has_bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_has_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_has_product`
--

DROP TABLE IF EXISTS `user_has_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_has_product` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `productId` bigint(3) NOT NULL,
  `userSourceId` bigint(10) NOT NULL,
  `beginningDate` datetime NOT NULL,
  `endingDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_has_provider_product1` (`productId`),
  KEY `fk_product_has_provider_user_source_id` (`userSourceId`)
) ENGINE=MyISAM AUTO_INCREMENT=46 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_has_product`
--

LOCK TABLES `user_has_product` WRITE;
/*!40000 ALTER TABLE `user_has_product` DISABLE KEYS */;
INSERT INTO `user_has_product` VALUES (8,1,1,'0000-00-00 00:00:00',NULL),(9,1,47,'0000-00-00 00:00:00',NULL),(10,2,47,'0000-00-00 00:00:00',NULL),(11,3,47,'0000-00-00 00:00:00',NULL),(12,2,47,'0000-00-00 00:00:00',NULL),(13,1,47,'0000-00-00 00:00:00',NULL),(14,3,47,'0000-00-00 00:00:00',NULL),(15,1,48,'0000-00-00 00:00:00',NULL),(16,3,48,'0000-00-00 00:00:00',NULL),(17,2,48,'0000-00-00 00:00:00',NULL),(18,2,49,'2019-09-17 13:54:56',NULL),(19,3,50,'2019-09-17 13:57:10',NULL),(20,2,50,'2019-09-17 13:57:10',NULL),(21,1,50,'2019-09-17 13:57:08',NULL),(22,1,379,'2019-09-18 15:40:25',NULL),(23,2,379,'2019-09-18 15:40:25',NULL),(24,3,379,'2019-09-18 15:40:25',NULL),(25,3,410,'2019-09-24 14:49:27',NULL),(26,2,410,'2019-09-24 14:49:27',NULL),(27,1,410,'2019-09-24 14:49:26',NULL),(28,1,430,'2019-09-25 11:03:05',NULL),(29,2,430,'2019-09-25 11:03:06',NULL),(30,3,430,'2019-09-25 11:03:06',NULL),(31,1,431,'2019-10-01 09:24:11',NULL),(32,3,431,'2019-10-01 09:24:12',NULL),(33,2,431,'2019-10-01 09:24:12',NULL),(34,2,432,'2019-10-01 09:27:24',NULL),(35,1,432,'2019-10-01 09:27:24',NULL),(36,3,432,'2019-10-01 09:27:24',NULL),(37,1,433,'2019-10-01 09:39:03',NULL),(38,3,433,'2019-10-01 09:39:03',NULL),(39,2,433,'2019-10-01 09:39:03',NULL),(40,1,434,'2019-10-01 09:49:53',NULL),(41,2,434,'2019-10-01 09:49:59',NULL),(42,3,434,'2019-10-01 09:50:00',NULL),(43,1,433,'2019-10-04 15:07:27',NULL),(44,3,433,'2019-10-04 15:07:27',NULL),(45,2,433,'2019-10-04 15:07:27',NULL);
/*!40000 ALTER TABLE `user_has_product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-10-08 13:32:58



-- Cambios hechos por moises data dummy

UPDATE `alodigaWallet`.`product` SET `symbol`='AL' WHERE `id`='1';
UPDATE `alodigaWallet`.`product` SET `symbol`='SA' WHERE `id`='2';
UPDATE `alodigaWallet`.`product` SET `symbol`='TP' WHERE `id`='3';
UPDATE `alodigaWallet`.`product` SET `symbol`='HC' WHERE `id`='4';
UPDATE `alodigaWallet`.`product` SET `symbol`='DL' WHERE `id`='5';
UPDATE `alodigaWallet`.`product` SET `symbol`='BS' WHERE `id`='6';







INSERT INTO `alodigaWallet`.`commission` (`productId`, `transactionTypeId`, `beginningDate`, `isPercentCommision`, `value`) VALUES ('1', '5', '2019-10-07 10:00:00', '1', '2.5');
INSERT INTO `alodigaWallet`.`commission` (`productId`, `transactionTypeId`, `beginningDate`, `isPercentCommision`, `value`) VALUES ('2', '5', '2019-10-07 10:00:00', '1', '2.5');




