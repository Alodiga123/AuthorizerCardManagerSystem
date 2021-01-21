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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance_history`
--

LOCK TABLES `balance_history` WRITE;
/*!40000 ALTER TABLE `balance_history` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission`
--

LOCK TABLES `commission` WRITE;
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
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
  `commissionId` bigint(20) NOT NULL,
  `amount` float(5,2) NOT NULL,
  `processedDate` datetime NOT NULL,
  `transactionId` bigint(20) NOT NULL,
  `isResidual` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_commission_item_transaction` (`transactionId`),
  KEY `fk_commission_item_commission` (`commissionId`),
  CONSTRAINT `fk_commission_item_commission` FOREIGN KEY (`commissionId`) REFERENCES `commission` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_commission_item_transaction_item` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_item`
--

LOCK TABLES `commission_item` WRITE;
/*!40000 ALTER TABLE `commission_item` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `county`
--

LOCK TABLES `county` WRITE;
/*!40000 ALTER TABLE `county` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creditcard_type`
--

LOCK TABLES `creditcard_type` WRITE;
/*!40000 ALTER TABLE `creditcard_type` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enterprise`
--

LOCK TABLES `enterprise` WRITE;
/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
INSERT INTO `enterprise` VALUES (1,1,47,'AlodigaUSA','htttp://www.alodiga.com','customercare@alodiga.com','5842419340025','Miami Florida','Miami Florida',1,'customercare@alodiga.com');
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_integration_type`
--

LOCK TABLES `payment_integration_type` WRITE;
/*!40000 ALTER TABLE `payment_integration_type` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_patner`
--

LOCK TABLES `payment_patner` WRITE;
/*!40000 ALTER TABLE `payment_patner` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_type`
--

LOCK TABLES `payment_type` WRITE;
/*!40000 ALTER TABLE `payment_type` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference`
--

LOCK TABLES `preference` WRITE;
/*!40000 ALTER TABLE `preference` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_field`
--

LOCK TABLES `preference_field` WRITE;
/*!40000 ALTER TABLE `preference_field` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_type`
--

LOCK TABLES `preference_type` WRITE;
/*!40000 ALTER TABLE `preference_type` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference_value`
--

LOCK TABLES `preference_value` WRITE;
/*!40000 ALTER TABLE `preference_value` DISABLE KEYS */;
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
  PRIMARY KEY (`id`),
  KEY `fk_product_service1` (`categoryId`),
  KEY `fk_product_integration_type1` (`productIntegrationTypeId`),
  KEY `fk_product_enterprise1` (`enterpriseId`),
  CONSTRAINT `fk_product_enterprise1` FOREIGN KEY (`enterpriseId`) REFERENCES `enterprise` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_integrationType1` FOREIGN KEY (`productIntegrationTypeId`) REFERENCES `product_integration_type` (`id`),
  CONSTRAINT `fk_product_service1` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,1,1,1,'Alocoin',0,1,'1001','htttp://www.alodiga.com','584241934005',0,1),(2,1,1,1,'Saldo Alodiga',0,1,'1001','htttp://www.alodiga.com','584241934005',0,1),(3,1,1,1,'Tarjeta Prepagada',0,0,'1001','htttp://www.alodiga.com','584241934005',0,1);
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider`
--

LOCK TABLES `provider` WRITE;
/*!40000 ALTER TABLE `provider` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
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
  `totalTax` float(20,2) NOT NULL,
  `totalAmount` float(20,2) DEFAULT NULL,
  `promotionAmount` float(20,2) NOT NULL DEFAULT '0.00',
  `totalAlopointsUsed` float(20,2) NOT NULL,
  `topUpDescription` varchar(1000) DEFAULT NULL,
  `billPaymentDescription` varchar(1000) DEFAULT NULL,
  `externalId` varchar(255) DEFAULT NULL,
  `additional` varchar(500) DEFAULT NULL,
  `additional2` varchar(500) DEFAULT NULL,
  `closeId` bigint(20) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Contiene el origen de la transaccion, si es por IVR, pagina ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_source`
--

LOCK TABLES `transaction_source` WRITE;
/*!40000 ALTER TABLE `transaction_source` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_type`
--

LOCK TABLES `transaction_type` WRITE;
/*!40000 ALTER TABLE `transaction_type` DISABLE KEYS */;
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
  PRIMARY KEY (`id`),
  KEY `fk_product_has_provider_product1` (`productId`),
  KEY `fk_product_has_provider_user_source_id` (`userSourceId`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_has_product`
--

LOCK TABLES `user_has_product` WRITE;
/*!40000 ALTER TABLE `user_has_product` DISABLE KEYS */;
INSERT INTO `user_has_product` VALUES (8,1,1),(9,1,47),(10,2,47),(11,3,47),(12,2,47),(13,1,47),(14,3,47),(15,1,48),(16,3,48),(17,2,48);
/*!40000 ALTER TABLE `user_has_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdrawal`
--

DROP TABLE IF EXISTS `withdrawal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `withdrawal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userSourceId` bigint(10) DEFAULT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `transactionId` bigint(3) DEFAULT NULL,
  `commisionId` bigint(20) DEFAULT NULL,
  `userHasBankId` bigint(20) DEFAULT NULL,
  `typeWithdrawalId` bigint(20) DEFAULT NULL,
  `additional` varchar(500) DEFAULT NULL,
  `additional2` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_withdrawal_has_product_id` (`productId`),
  KEY `fk_withdrawal_has_transaction_id` (`transactionId`),
  KEY `fk_withdrawal_has_commision_id` (`commisionId`),
  KEY `fk_withdrawal_has_type_withdrawal_id` (`typeWithdrawalId`),
  CONSTRAINT `fk_withdrawal_has_commision_id` FOREIGN KEY (`commisionId`) REFERENCES `commission` (`id`),
  CONSTRAINT `fk_withdrawal_has_product_id` FOREIGN KEY (`productId`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_withdrawal_has_transaction_id` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`id`),
  CONSTRAINT `fk_withdrawal_has_type_withdrawal_id` FOREIGN KEY (`typeWithdrawalId`) REFERENCES `withdrawal_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `withdrawal`
--

LOCK TABLES `withdrawal` WRITE;
/*!40000 ALTER TABLE `withdrawal` DISABLE KEYS */;
/*!40000 ALTER TABLE `withdrawal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdrawal_type`
--

DROP TABLE IF EXISTS `withdrawal_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `withdrawal_type` (
  `id` bigint(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--Se modificó el campo totalAmount para que no permita nulos
--fecha de modificación: 16/09/2019, analista: Jesús Gómez

ALTER TABLE `alodigaWallet`.`transaction` 
CHANGE COLUMN `totalAmount` `totalAmount` FLOAT(20,2) NOT NULL ;

--Se agregó el campo concept a la tabla transaction
--fecha de modificación: 20/09/2019, analista: Jesús Gómez

ALTER TABLE `alodigaWallet`.`transaction` 
ADD COLUMN `concept` VARCHAR(255) NOT NULL AFTER `creationDate`;


--Se agregaron las tablas exchange_rate y exchange-detail a la BD
--fecha de modificaciÃ³n: 26/09/2019, analista: JesÃºs GÃ³mez

CREATE TABLE IF NOT EXISTS `alodigaWallet`.`exchange_rate` (
  `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
  `productId` BIGINT(3) NOT NULL,
  `value` FLOAT NOT NULL,
  `beginningDate` DATETIME NULL,
  `endingDate` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_exchange_rate_product1_idx` (`productId` ASC),
  CONSTRAINT `fk_exchange_rate_product1`
    FOREIGN KEY (`productId`)
    REFERENCES `alodigaWallet`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `alodigaWallet`.`exchange_detail` (
  `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
  `productId` BIGINT(3) NOT NULL,
  `exchangeRateId` BIGINT(10) NOT NULL,
  `transactionId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_exchange_detail_product1_idx` (`productId` ASC),
  INDEX `fk_exchange_detail_exchange_rate1_idx` (`exchangeRateId` ASC),
  INDEX `fk_exchange_detail_transaction1_idx` (`transactionId` ASC),
  CONSTRAINT `fk_exchange_detail_product1`
    FOREIGN KEY (`productId`)
    REFERENCES `alodigaWallet`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_exchange_detail_exchange_rate1`
    FOREIGN KEY (`exchangeRateId`)
    REFERENCES `alodigaWallet`.`exchange_rate` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_exchange_detail_transaction1`
    FOREIGN KEY (`transactionId`)
    REFERENCES `alodigaWallet`.`transaction` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
--
-- Dumping data for table `withdrawal_type`
--

LOCK TABLES `withdrawal_type` WRITE;
/*!40000 ALTER TABLE `withdrawal_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `withdrawal_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-12  9:42:22

//Yamelis 10-10-2019
INSERT INTO `alodigawallet`.`transaction_type` (`id`, `value`) VALUES ('7', 'TOP_UP_RECHARGE');

//Yamelis 12-10-2019
INSERT INTO `alodigawallet`.`product` (`id`, `enterpriseId`, `categoryId`, `productIntegrationTypeId`, `name`, `taxInclude`, `enabled`, `referenceCode`, `ratesUrl`, `accessNumberUrl`, `isFree`, `isAlocashProduct`, `symbol`) VALUES ('7', '1', '1', '1', 'Top Up', '0', '1', '1001', 'htttp://www.alodiga.com', '584241934005', '0', '1','TP');

