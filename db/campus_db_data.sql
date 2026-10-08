-- MySQL dump 10.13  Distrib 8.4.5, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: campus_db
-- ------------------------------------------------------
-- Server version	5.5.25a

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
-- Current Database: `campus_db`
--

/*!40000 DROP DATABASE IF EXISTS `campus_db`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `campus_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `campus_db`;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `receiver` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` int(11) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,1,'张三','13812340001','北京市','北京市','海淀区','xx大学学生公寓1号楼101室',1,'2026-04-01 06:53:43'),(2,1,'张三','13812340001','河北省','石家庄市','长安区','xx小区3号楼2单元501',0,'2026-04-01 06:53:43'),(3,2,'虎一铭','13899767523','四川省','成都市','犀浦街道','西南交通大学19-4030',1,'2026-04-01 06:53:43'),(5,3,'赵美婷','13712340003','四川省','成都市','武侯区','大悦城',1,'2026-04-01 06:53:43'),(7,5,'周婷','13512340005','湖北省','武汉市','洪山区','xx大学南湖校区3栋206室',1,'2026-04-01 06:53:43'),(8,5,'周婷','13512340005','湖北省','宜昌市','西陵区','xx路xx花园5栋402',0,'2026-04-01 06:53:43'),(10,7,'peter','13679950000','新疆维吾尔自治区','伊犁哈萨克自治州','rggf','sddxv ',NULL,'2026-04-02 03:49:26'),(11,4,'徐娅','152-6986-4589','四川省','成都市','郫都区','西南交大天佑斋15栋',1,'2026-04-02 11:37:46'),(12,2,'huyiming','10010','四川省','成都市','郫都区','西南交大天佑斋15栋',0,'2026-04-03 08:50:17');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (20,3,3,'2026-04-11 00:20:07'),(21,3,69,'2026-04-11 00:20:13'),(22,3,53,'2026-04-11 00:20:16'),(23,3,55,'2026-04-11 00:20:19'),(24,3,73,'2026-04-11 00:20:23'),(25,3,35,'2026-04-11 00:20:28'),(26,3,97,'2026-04-11 00:24:56'),(27,3,49,'2026-04-11 00:25:59'),(28,3,63,'2026-04-11 00:26:07'),(30,2,29,'2026-04-11 00:30:23'),(33,2,57,'2026-05-06 00:28:34'),(36,2,6,'2026-06-12 00:47:08');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collect`
--

DROP TABLE IF EXISTS `collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `collect_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `collect_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collect`
--

LOCK TABLES `collect` WRITE;
/*!40000 ALTER TABLE `collect` DISABLE KEYS */;
INSERT INTO `collect` VALUES (19,3,132,'2026-04-11 00:20:49'),(21,3,4,'2026-04-11 00:24:27'),(22,3,20,'2026-04-11 00:24:45'),(23,3,82,'2026-04-11 00:24:50'),(24,3,21,'2026-04-11 00:24:52'),(25,3,97,'2026-04-11 00:25:04'),(26,3,49,'2026-04-11 00:26:02'),(27,3,63,'2026-04-11 00:26:06'),(31,2,105,'2026-04-11 00:30:28'),(32,2,101,'2026-04-11 00:30:36'),(33,2,79,'2026-04-11 00:30:40'),(35,2,20,'2026-04-11 01:36:21'),(36,4,44,'2026-04-12 18:13:30'),(39,2,7,'2026-05-03 00:50:22'),(40,2,6,'2026-06-12 00:47:10'),(41,2,63,'2026-08-11 05:46:39');
/*!40000 ALTER TABLE `collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `value` decimal(38,2) DEFAULT NULL,
  `min_amount` decimal(38,2) DEFAULT NULL,
  `stock` int(11) DEFAULT '0',
  `is_active` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,'8.5折优惠券','discount',0.85,0.00,57,1,'2026-04-08 21:27:08'),(2,'满200减80','cash',80.00,200.00,72,1,'2026-04-08 21:27:08'),(3,'满800减300','cash',300.00,800.00,75,1,'2026-04-08 21:27:08'),(4,'9折优惠券','discount',0.90,0.00,66,1,'2026-04-08 21:27:08'),(5,'幸运免单券！！','discount',0.00,0.00,31,1,'2026-04-08 21:25:03');
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation`
--

DROP TABLE IF EXISTS `evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `from_user_id` int(11) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `images` varchar(255) DEFAULT NULL,
  `reply` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `reply_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  KEY `from_user_id` (`from_user_id`),
  KEY `to_user_id` (`to_user_id`),
  CONSTRAINT `evaluation_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `evaluation_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `evaluation_ibfk_3` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `evaluation_ibfk_4` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation`
--

LOCK TABLES `evaluation` WRITE;
/*!40000 ALTER TABLE `evaluation` DISABLE KEYS */;
INSERT INTO `evaluation` VALUES (8,70,79,2,4,5,'非常好用哈哈哈哈哈就是便宜点就好了',NULL,NULL,'2026-04-14 09:41:36',NULL),(9,73,3,3,2,5,'商品非常好，攒劲得很，好用！',NULL,NULL,'2026-04-21 09:06:48',NULL),(10,69,48,2,4,2,'一点都不好穿，差评',NULL,NULL,'2026-05-03 08:50:57',NULL);
/*!40000 ALTER TABLE `evaluation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gift_pack`
--

DROP TABLE IF EXISTS `gift_pack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gift_pack` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seller_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `product_ids` varchar(255) DEFAULT NULL,
  `discount` decimal(38,2) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `is_active` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `seller_id` (`seller_id`),
  CONSTRAINT `gift_pack_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gift_pack`
--

LOCK TABLES `gift_pack` WRITE;
/*!40000 ALTER TABLE `gift_pack` DISABLE KEYS */;
INSERT INTO `gift_pack` VALUES (21,4,'学习礼包','大学物理或者概率论会使用计算器，都挺新的','20,21,22,24',0.85,'graduation',NULL,1,'2026-04-11 08:07:14'),(22,4,'体育包','买球送健身卡一张','81,82,80,96',0.85,'graduation',NULL,1,'2026-04-11 08:08:14'),(23,4,'化妆礼包','化妆工具直接装收纳包，便宜出了','97,104,95,106',0.85,'graduation',NULL,1,'2026-04-11 08:09:19'),(24,2,'考研党严选','一本数学考研书+四六级全套，送两支笔，另外闹钟可以每天早上叫醒你哈哈哈哈哈！','1,4,26,35',0.85,'graduation',NULL,1,'2026-04-11 08:11:33'),(25,2,'性价比之王·小家电','在外面住的同学肯定是需要冰箱的噻，还有空气炸锅','75,76,70,78',0.85,'graduation',NULL,1,'2026-04-11 08:12:54'),(26,2,'夏天必入','骑上我心爱的小摩托~再用ccd美美出片！','53,69,51,67',0.85,'graduation',NULL,1,'2026-04-11 08:15:17'),(27,3,'全家桶','毕业了，8成新，便宜出了，新生可以看看，比买新的划算，还能再用4年！有免单卡，大家放心冲！','7,118,116,66',0.85,'graduation',NULL,1,'2026-04-11 08:17:10'),(28,3,'宿舍闲置','带不走了，送点福利给大家，学弟学妹们快来买！','37,32,38,31',0.85,'graduation',NULL,1,'2026-04-11 08:18:08'),(29,3,'时尚达人严选','新买的，完全没穿过，不挑身材，比实体店官网都便宜','41,40,42,122',0.85,'graduation',NULL,1,'2026-04-11 08:19:51'),(30,4,'开学大礼包','很好用','49,22,24,106',0.90,'freshman',NULL,1,'2026-05-19 09:31:48');
/*!40000 ALTER TABLE `gift_pack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fromUserId` int(11) NOT NULL COMMENT '发送者用户ID',
  `toUserId` int(11) NOT NULL COMMENT '接收者用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `isRead` int(11) DEFAULT '0' COMMENT '是否已读 0未读 1已读',
  `productId` int(11) DEFAULT NULL COMMENT '关联商品ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (68,1,2,'我对这个宝贝感兴趣：《简约垃圾桶》\n商品链接：http://localhost:5173/product/detail/32','2026-04-10 17:47:45',1,32),(69,1,2,'我对这个宝贝感兴趣：《简约垃圾桶》\n商品链接：http://localhost:5173/product/detail/32','2026-04-10 17:48:22',1,32),(70,1,2,'这个商品还在不','2026-04-10 17:48:32',1,32),(71,1,2,'😀','2026-04-10 17:48:38',1,32),(72,2,1,'📍 我的位置：30.771488299999998, 103.97742910000002\nhttps://uri.amap.com/marker?position=103.97742910000002,30.771488299999998','2026-04-10 17:49:00',1,NULL),(73,2,1,'📍 我的位置：30.771488299999998, 103.97742910000002\nhttps://uri.amap.com/marker?position=103.97742910000002,30.771488299999998','2026-04-10 17:49:00',1,NULL),(74,2,1,'好的，谢谢！','2026-04-10 17:49:05',1,NULL),(76,3,2,'在吗？','2026-04-11 08:26:34',1,4),(77,3,2,'🤣','2026-04-11 08:26:41',1,4),(78,3,2,'我对这个宝贝感兴趣：《四六级真题全套》\n商品链接：http://localhost:5173/product/detail/4','2026-04-11 08:26:44',1,4),(79,3,2,'这书还在不，我要准备冲六级了，但是一点没学！','2026-04-11 08:27:05',1,4),(80,3,2,'可以自提码？咱都是交大的','2026-04-11 08:27:17',1,4),(81,3,4,'我去，明天就概率论考试了，你这个计算器还在不，我还没买呢','2026-04-11 08:27:44',1,22),(82,3,4,'快回我','2026-04-11 08:27:47',1,22),(83,2,3,'还在，不过写了几套，你介意吗？','2026-04-11 08:28:27',1,NULL),(84,2,3,'我去，那你赶紧学啊','2026-04-11 08:28:34',1,NULL),(85,2,3,'马上就六级考试了','2026-04-11 08:28:54',1,NULL),(86,2,3,'👍','2026-04-11 08:29:10',1,NULL),(87,2,4,'我对这个宝贝感兴趣：《便利贴》\n商品链接：http://localhost:5173/product/detail/20','2026-04-11 09:35:01',1,20),(88,2,4,'在吗？','2026-04-11 09:35:07',1,20),(89,2,4,'再不？','2026-04-11 09:36:33',1,20),(90,3,2,'什么时候发货？','2026-04-11 15:58:55',1,NULL),(91,3,2,'📍 我的位置：30.771477559396807, 103.97743508768474\nhttps://uri.amap.com/marker?position=103.97743508768474,30.771477559396807','2026-04-11 15:59:11',1,NULL),(92,2,3,'明天去1教咋样？','2026-04-11 16:29:02',1,NULL),(93,2,3,'😘','2026-04-11 16:29:08',1,NULL),(94,2,4,'我对这个宝贝感兴趣：《便利贴》\n商品链接：http://localhost:5173/product/detail/20','2026-04-11 16:30:08',1,20),(95,2,4,'我对这个宝贝感兴趣：《训练篮球》\n商品链接：http://localhost:5173/product/detail/81','2026-04-11 16:30:16',1,81),(96,2,2,'1','2026-04-12 08:38:07',1,NULL),(97,4,1,'在吗？','2026-04-13 02:13:52',1,44),(98,3,2,'ok,那我现在下单，线下交易','2026-04-13 02:25:47',1,NULL),(99,3,3,'你好','2026-04-21 09:15:07',1,NULL),(100,3,3,'可以拍实物图吗？','2026-04-21 09:15:41',1,NULL),(101,3,3,'能便宜点吗？','2026-04-21 09:15:46',1,NULL),(102,3,3,'在吗？','2026-04-21 09:15:53',1,NULL),(103,2,2,'1','2026-04-27 06:54:54',1,NULL),(104,2,4,'可以拍实物图吗？','2026-05-06 09:27:59',1,NULL),(105,4,2,'不能','2026-05-06 09:28:52',1,NULL),(106,2,4,'OK，再见！','2026-05-08 06:45:33',1,NULL),(107,3,2,'hi','2026-05-08 08:37:22',1,NULL),(108,2,3,'咋了','2026-05-08 08:37:31',1,NULL),(109,3,2,'没事','2026-05-08 08:37:40',1,NULL),(110,2,3,'没事说啥呢','2026-05-08 08:38:12',1,NULL),(111,2,3,'你好','2026-05-08 08:38:27',1,NULL),(112,4,2,'我对这个宝贝感兴趣：《棉线绳》\n商品链接：http://localhost:5173/product/detail/34','2026-05-13 15:22:29',1,34),(113,4,1,'好的，谢谢！','2026-05-13 15:42:16',0,NULL),(114,4,1,'📍 我的位置：30.771495499999997, 103.9774135\nhttps://uri.amap.com/marker?position=103.9774135,30.771495499999997','2026-05-13 15:42:20',0,NULL),(115,2,4,'不卖！你都不给我拍实物图','2026-06-09 12:54:55',1,NULL),(116,2,4,'📍 我的位置：30.771450625, 103.97741525\nhttps://uri.amap.com/marker?position=103.97741525,30.771450625','2026-06-09 12:55:12',1,NULL),(117,2,4,'🥳','2026-06-09 12:55:27',1,NULL),(118,2,4,'💯','2026-06-09 12:55:30',1,NULL),(119,4,2,'干啥','2026-06-09 12:55:51',1,NULL),(120,4,2,'算了算了','2026-06-09 12:55:54',1,NULL),(121,4,2,'好的，谢谢！','2026-06-09 12:55:58',1,NULL),(122,4,3,'不想回','2026-06-09 12:56:08',0,NULL),(123,2,3,'我对这个宝贝感兴趣：《头戴式耳机》\n商品链接：http://localhost:5173/product/detail/61','2026-06-12 08:43:41',0,61),(124,2,3,'你这个卖不卖','2026-06-12 08:43:50',0,61),(125,2,3,'你好','2026-06-12 08:47:17',0,6),(126,2,3,'📍 我的位置：30.76534773285491, 103.98920534515528\nhttps://uri.amap.com/marker?position=103.98920534515528,30.76534773285491','2026-06-12 08:47:22',0,6),(127,2,3,'好的，谢谢！','2026-06-12 08:47:23',0,6),(128,2,3,'😡','2026-06-12 08:47:26',0,6),(129,2,4,'我对这个宝贝感兴趣：《二手智能手机》\n商品链接：http://localhost:5173/product/detail/63','2026-08-11 13:45:49',1,63),(130,2,4,'老板这个多少钱','2026-08-11 13:45:56',1,63),(131,2,4,'便宜出马？','2026-08-11 13:46:01',1,63),(132,2,4,'📍 我的位置：43.9771, 81.5274\nhttps://uri.amap.com/marker?position=81.5274,43.9771','2026-08-11 13:46:07',1,63),(133,2,4,'什么时候发货？','2026-08-11 13:46:30',1,63),(134,2,4,'🤣','2026-08-11 13:46:34',1,63),(135,4,2,'可以可以','2026-08-11 13:58:10',0,NULL);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `need`
--

DROP TABLE IF EXISTS `need`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `need` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `need_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `need`
--

LOCK TABLES `need` WRITE;
/*!40000 ALTER TABLE `need` DISABLE KEYS */;
INSERT INTO `need` VALUES (7,2,'exchange','最新17pro','谁有别的颜色，我可以换','2026-04-10 06:01:57','1775800879430_phone2.jpg','数码产品'),(8,2,'want','求相机','有没有同款ccd哇，便宜一点的','2026-04-12 08:37:53','1775983037441_xiangji3.jpg','数码产品'),(10,3,'have','airpod95新','300出，原价大家都知道的这个算最便宜的了','2026-04-21 09:14:56','1776762857733_erji1.jpg','数码产品'),(11,3,'have','出相机','去年买的还没用','2026-06-08 12:05:15','1780920306595_xiangji1.jpg','数码产品'),(12,2,'want','收篮球','收一个篮球，便宜点，带价来','2026-06-08 12:31:41',NULL,'运动器材'),(13,1,'want','x想要一个17pro','如题','2026-06-08 12:49:12',NULL,'数码产品'),(14,4,'have','出售篮球','李宁95新篮球，直接出','2026-06-09 12:02:20','1781006523244_basketball2.jpg','运动器材');
/*!40000 ALTER TABLE `need` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) DEFAULT NULL,
  `product_id` int(11) NOT NULL,
  `buyer_id` int(11) NOT NULL,
  `seller_id` int(11) NOT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT '1',
  `total_amount` decimal(38,2) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  `pickup_point` varchar(255) DEFAULT NULL,
  `trade_type` varchar(255) DEFAULT NULL,
  `pay_type` varchar(255) DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  `pay_status` varchar(255) DEFAULT NULL,
  `delivery_status` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `ship_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `user_coupon_id` int(11) DEFAULT NULL,
  `discount_amount` decimal(38,2) DEFAULT NULL,
  `refund_status` varchar(20) DEFAULT 'none',
  `refund_time` datetime DEFAULT NULL,
  `refund_amount` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `product_id` (`product_id`),
  KEY `buyer_id` (`buyer_id`),
  KEY `seller_id` (`seller_id`),
  KEY `address_id` (`address_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`),
  CONSTRAINT `orders_ibfk_4` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (68,'ORD1776047048178d9030e92',32,2,3,19.90,1,19.90,3,NULL,'online','wallet','completed','paid','received',NULL,'2026-04-13 02:24:08','2026-04-13 02:24:08','2026-04-13 02:25:18','2026-05-03 08:17:27',NULL,0.00,'none',NULL,NULL),(69,'ORD17760470721765f9ac772',48,2,4,199.90,1,199.90,3,NULL,'online','wallet','completed','paid','received',NULL,'2026-04-13 02:24:32','2026-04-13 02:24:32','2026-04-13 02:26:17','2026-04-13 02:53:16',NULL,0.00,'rejected','2026-05-03 08:33:35',NULL),(70,'ORD17760470829951258b6a3',79,2,4,299.00,1,299.00,3,NULL,'online','wallet','refunded','paid','received',NULL,'2026-04-13 02:24:42','2026-04-13 02:24:43','2026-04-13 02:26:14',NULL,NULL,0.00,'approved','2026-05-03 08:23:23',299.00),(71,'ORD17760473392025f4337bd',85,2,3,39.00,1,39.00,3,NULL,'offline','offline','cancelled','unpaid','pending',NULL,'2026-04-13 02:28:59',NULL,NULL,NULL,NULL,0.00,'none',NULL,NULL),(72,'ORD1776047958924df331f80',21,2,4,19.90,1,19.90,3,NULL,'online','wallet','refunded','paid','pending',NULL,'2026-04-13 02:39:18','2026-04-13 02:39:19',NULL,NULL,NULL,0.00,'approved','2026-04-13 02:45:00',19.90),(73,'ORD177659890354389f4eca0',3,3,2,90.60,1,90.60,5,NULL,'online','wallet','completed','paid','received',NULL,'2026-04-19 11:41:43','2026-04-19 11:41:43','2026-04-19 13:45:20','2026-04-19 13:56:18',NULL,0.00,'none',NULL,NULL),(74,'ORD177660653797708f0ede8',42,3,2,129.90,1,129.90,5,NULL,'online','wallet','completed','paid','received',NULL,'2026-04-19 13:48:57','2026-04-19 13:48:58','2026-05-03 08:17:33','2026-05-08 08:39:04',NULL,0.00,'none',NULL,NULL),(75,'ORD17766067955310969e8c8',21,3,4,19.90,1,19.90,5,NULL,'online','wallet','refunded','paid','pending',NULL,'2026-04-19 13:53:15','2026-04-19 13:53:15',NULL,NULL,NULL,0.00,'approved','2026-04-19 13:53:23',19.90),(76,'ORD1777797824541cf0b9d52',20,2,4,5.90,1,5.90,12,NULL,'online','wallet','completed','paid','received',NULL,'2026-05-03 08:43:44','2026-05-03 08:43:44','2026-05-03 08:45:15','2026-05-03 08:45:49',NULL,0.00,'none',NULL,NULL),(77,'ORD1778056086802b96c1230',86,2,3,299.00,1,299.00,3,NULL,'offline','offline','cancelled','unpaid','pending',NULL,'2026-05-06 08:28:06',NULL,NULL,NULL,NULL,0.00,'none',NULL,NULL),(78,'ORD177805609842061b7d2b1',124,2,4,199.00,1,199.00,3,NULL,'online','wallet','refunded','paid','received',NULL,'2026-05-06 08:28:18','2026-05-06 08:28:18','2026-05-06 08:29:40',NULL,NULL,0.00,'approved','2026-05-06 08:31:33',199.00),(79,'ORD177805611767527eabd98',57,2,1,549.00,1,549.00,3,NULL,'online','wallet','refunded','paid','pending',NULL,'2026-05-06 08:28:37','2026-05-06 08:28:37',NULL,NULL,NULL,0.00,'approved','2026-05-06 08:28:41',549.00),(80,'ORD1778059018052155adc75',85,2,3,39.00,1,39.00,3,NULL,'online','wallet','shipped','paid','shipped',NULL,'2026-05-06 09:16:58','2026-05-06 09:16:58','2026-05-06 09:17:20',NULL,NULL,0.00,'none',NULL,NULL),(81,'ORD17780593926408df8ab4c',21,2,4,19.90,1,19.90,12,NULL,'online','wallet','paid','paid','pending',NULL,'2026-05-06 09:23:12','2026-05-06 09:23:12',NULL,NULL,NULL,0.00,'none',NULL,NULL),(82,'ORD177807278706899a46854',95,2,4,59.00,1,59.00,12,NULL,'online','wallet','paid','paid','pending',NULL,'2026-05-06 13:06:27','2026-05-06 13:06:27',NULL,NULL,NULL,0.00,'none',NULL,NULL),(83,'ORD17782298830531d312d93',47,4,3,99.90,1,99.90,11,NULL,'online','wallet','paid','paid','pending',NULL,'2026-05-08 08:44:43','2026-05-08 08:44:43',NULL,NULL,NULL,0.00,'none',NULL,NULL),(84,'ORD177927764867748989798',22,2,4,15.90,1,15.90,3,'1号教学楼前','offline','offline','refunded','unpaid','pending',NULL,'2026-05-20 11:47:28',NULL,'2026-05-20 11:48:07',NULL,NULL,0.00,'approved','2026-05-20 11:51:16',15.90),(85,'ORD1781254083139da53f286',22,2,4,15.90,1,15.90,12,NULL,'online','wallet','paid','paid','pending',NULL,'2026-06-12 08:48:03','2026-06-12 08:48:03',NULL,NULL,NULL,0.00,'none',NULL,NULL),(86,'ORD1786456013974e46b1f71',63,2,4,899.00,1,899.00,3,NULL,'online','wallet','paid','paid','pending',NULL,'2026-08-11 13:46:53','2026-08-11 13:46:54',NULL,NULL,NULL,0.00,'none',NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `info` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `hot` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL COMMENT '发布人ID，关联user表id',
  `status` int(11) DEFAULT NULL,
  `buyer_id` int(11) DEFAULT NULL COMMENT '成交买家ID，未售出为NULL',
  PRIMARY KEY (`id`),
  KEY `FK979liw4xk18ncpl87u4tygx2u` (`user_id`),
  CONSTRAINT `FK979liw4xk18ncpl87u4tygx2u` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'考研数学复习书',29.90,'book1.jpg','九成新，无笔记，数学一适用','学习用品',1,2,0,NULL),(2,'全新护眼台灯',55.00,'light1.jpg','宿舍可用，三档调光','生活用品',0,2,0,NULL),(3,'机械键盘',90.60,'keyboard1.jpg','非常好用','数码产品',0,2,1,3),(4,'四六级真题全套',36.80,'book2.jpg','25新版，只做过一套','学习用品',0,2,0,NULL),(6,'充电宝',26.30,'power1.jpg','便宜出','数码产品',1,3,0,NULL),(7,'iphone15pro',5099.00,'phone1.jpg','95新，电池健康度90%+，适合学生党入手','数码产品',0,3,0,NULL),(16,'碳素笔',5.60,'pen1.jpg','肯定有人要','学习用品',0,3,0,NULL),(17,'鼠标',95.60,'mouse1.jpg','还行吧，9成新','数码产品',0,3,0,NULL),(20,'便利贴',5.90,'bianlitie.jpg','学生党必备便利贴，可用于记笔记、做标记，纸张厚实，书写流畅','学习用品',0,4,1,2),(21,'学生计算器',19.90,'calculator1.jpg','多功能学生计算器，适用于数学、统计等计算，按键灵敏，续航持久','学习用品',0,4,1,2),(22,'白色便携计算器',15.90,'calculator2.jpg','轻薄便携计算器，学生考试、日常计算都能用，外观简约','学习用品',1,4,1,2),(24,'固体胶棒',4.90,'glue_stick.jpg','高粘度固体胶，手工、办公、学生用都合适，粘力强，不易干','学习用品',0,4,0,NULL),(25,'黑色马克笔',6.90,'mark_pen.jpg','学生用黑色马克笔，笔头顺滑，色彩浓郁，适合绘画、标记','学习用品',0,2,0,NULL),(26,'黑色中性笔',3.90,'pen1.jpg','学生考试专用中性笔，书写流畅，不断墨，握感舒适','学习用品',0,2,0,NULL),(27,'复古木质钢笔',29.90,'pen2.jpg','复古木质钢笔，书写顺滑，适合学生练字、日常书写','学习用品',0,2,0,NULL),(28,'透明刻度直尺',4.90,'ruler1.jpg','学生用透明直尺，刻度清晰，材质耐用，测量精准','学习用品',0,2,0,NULL),(29,'蓝色修正带',7.90,'xiuzhengdai.jpg','学生用大容量修正带，涂改无痕，带芯顺滑，不易断带','学习用品',1,3,0,NULL),(30,'多功能插排',29.90,'chapai.jpg','学生宿舍/家用多功能插排，多口设计，安全耐用，满足日常用电需求','生活用品',0,3,0,NULL),(31,'家用吹风机',49.90,'chuifengji.jpg','大功率家用吹风机，速干不伤发，宿舍/家用都适用，轻便易携','生活用品',0,3,0,NULL),(32,'简约垃圾桶',19.90,'lajitong.jpg','家用简约垃圾桶，大容量设计，密封性好，防异味，适合宿舍/家庭使用','生活用品',0,3,1,2),(33,'铁艺收纳篮',25.90,'lanzi.jpg','铁艺镂空收纳篮，可用于收纳杂物、水果，颜值高，实用性强','生活用品',0,2,0,NULL),(34,'棉线绳',5.90,'mianqian.jpg','家用棉线绳，可用于捆绑、手工、晾晒，结实耐用，用途广泛','生活用品',0,2,0,NULL),(35,'学生闹钟',39.90,'naozhong.jpg','学生专用静音闹钟，走时精准，灯光柔和，叫醒不吵人','生活用品',0,2,0,NULL),(36,'家用陶瓷盘',15.90,'panzi.jpg','家用陶瓷餐盘，颜值高，耐高温，可用于微波炉，适合学生/家庭使用','生活用品',0,1,0,NULL),(37,'护眼台灯',59.90,'taideng.jpg','学生护眼台灯，无蓝光，多档调光，保护视力，宿舍学习必备','生活用品',1,3,0,NULL),(38,'可移动置物架',89.90,'zhiwujia.jpg','多层可移动置物架，大容量收纳，可用于厨房、宿舍、客厅，万向轮设计','生活用品',0,3,0,NULL),(39,'创意烛台',35.90,'zhutai.jpg','创意水波纹烛台，氛围感拉满，可用于装饰、香薰，提升生活质感','生活用品',0,3,0,NULL),(40,'休闲粉色外套',89.90,'coat1.jpg','学生百搭粉色休闲外套，面料舒适，日常通勤、校园穿搭都合适','服饰',0,2,0,NULL),(41,'简约条纹外套',79.90,'coat2.jpg','日系简约条纹外套，宽松版型，不挑身材，学生日常穿搭','服饰',0,2,0,NULL),(42,'通勤棕色夹克',129.90,'coat3.jpg','职场通勤棕色夹克，质感挺括，学生面试、日常穿搭两用','服饰',0,2,1,3),(43,'清新浅绿连衣裙',99.90,'dress.jpg','清新浅绿连衣裙，版型显瘦，适合学生约会、日常出行','服饰',1,1,0,NULL),(44,'简约金属项链',49.90,'necklace.jpg','小众设计感金属项链，百搭不挑人，提升穿搭精致感','服饰',0,1,0,NULL),(45,'素圈简约戒指',29.90,'ring.jpg','极简素圈戒指，百搭款，日常佩戴、搭配穿搭都合适','服饰',1,3,0,NULL),(46,'休闲低帮鞋',159.90,'shoe1.jpg','学生百搭休闲低帮鞋，脚感舒适，日常通勤、校园穿搭','服饰',0,3,0,NULL),(47,'简约懒人鞋',99.90,'shoe2.jpg','一脚蹬懒人鞋，方便穿脱，学生日常出行、宿舍穿都合适','服饰',1,3,1,4),(48,'复古板鞋',199.90,'shoe3.jpg','复古潮流板鞋，颜值高，脚感舒适，学生穿搭出街必备','服饰',0,4,1,2),(49,'蝴蝶手链',69.90,'shoulian.jpg','清新蝴蝶设计手链，小众精致，学生日常佩戴、送礼都合适','服饰',0,4,0,NULL),(50,'轻便电动车',1999.00,'diandongche1.jpg','学生通勤轻便电动车，续航持久，颜值高，校园代步神器','交通出行',1,4,0,NULL),(51,'小型电动车',1599.00,'diandongche2.jpg','小型代步电动车，操作简单，适合学生日常出行','交通出行',0,2,0,NULL),(52,'潮流滑板',299.00,'huaban1.jpg','学生潮流双翘滑板，刷街代步，颜值拉满，适合新手入门','交通出行',0,2,0,NULL),(53,'入门滑板',199.00,'huaban2.jpg','新手入门滑板，做工扎实，适合学生练习、代步','交通出行',0,2,0,NULL),(54,'专业滑板',399.00,'huaban3.jpg','专业刷街滑板，性能强劲，适合进阶玩家','交通出行',0,2,0,NULL),(55,'复古女式自行车',599.00,'zixingche1.jpg','复古通勤女式自行车，颜值高，适合学生日常骑行','交通出行',0,2,0,NULL),(56,'折叠自行车',699.00,'zixingche2.jpg','便携折叠自行车，方便收纳，适合学生宿舍使用','交通出行',0,1,0,NULL),(57,'粉色通勤自行车',549.00,'zixingche3.jpg','粉色清新通勤自行车，适合女生校园骑行','交通出行',1,1,0,NULL),(58,'公路自行车',1999.00,'zixingche4.jpg','专业公路自行车，速度快，适合骑行爱好者','交通出行',0,3,0,NULL),(59,'山地自行车',1799.00,'zixingche5.jpg','越野山地自行车，适应多种路况，学生户外骑行','交通出行',1,3,0,NULL),(60,'无线蓝牙耳机',199.00,'erji1.jpg','真无线蓝牙耳机，降噪强劲，续航持久，学生学习、通勤必备','数码产品',0,3,0,NULL),(61,'头戴式耳机',299.00,'erji2.jpg','头戴式降噪耳机，音质出色，适合学生上网课、听歌','数码产品',0,3,0,NULL),(62,'便携蓝牙耳机',129.00,'erji3.jpg','小巧便携蓝牙耳机，颜值高，适合学生日常使用','数码产品',0,4,0,NULL),(63,'二手智能手机',899.00,'phone1.jpg','二手九成新智能手机，性能流畅，学生备用机首选','数码产品',1,4,1,2),(64,'iphone17 pro max',1999.00,'phone2.jpg','二手苹果手机，成色好，系统流畅，学生日常使用','数码产品',0,4,0,NULL),(65,'机械手表',599.00,'watch1.jpg','商务机械手表，走时精准，适合学生面试、日常佩戴','数码产品',0,4,0,NULL),(66,'智能手表',399.00,'watch2.jpg','智能运动手表，监测健康，学生运动、日常使用','数码产品',0,3,0,NULL),(67,'复古相机',499.00,'xiangji1.jpg','复古胶片相机，拍照有质感，学生摄影爱好者必备','数码产品',0,2,0,NULL),(68,'DV摄像机',399.00,'xiangji2.jpg','复古DV摄像机，记录生活，学生vlog拍摄神器','数码产品',1,2,0,NULL),(69,'CCD相机',299.00,'xiangji3.jpg','复古CCD相机，拍照氛围感拉满，学生日常拍照','数码产品',0,2,0,NULL),(70,'小型冰箱',599.00,'bingxiang1.jpg','宿舍小型冰箱，冷藏冷冻，学生宿舍必备','小家电',0,2,0,NULL),(71,'双门冰箱',1299.00,'bingxiang2.jpg','家用双门冰箱，大容量，适合租房、家庭使用','小家电',0,1,0,NULL),(72,'落地电风扇',159.00,'dianfengshan.jpg','家用落地电风扇，风力大，静音设计，宿舍/家用都适用','小家电',0,1,0,NULL),(73,'小型电煮锅',99.00,'dianzhuguo1.jpg','学生宿舍电煮锅，多功能，可煮可炒，一人食神器','小家电',0,1,0,NULL),(74,'大容量电煮锅',129.00,'dianzhuguo2.jpg','大容量电煮锅，适合多人聚餐，学生宿舍使用','小家电',0,2,0,NULL),(75,'分体式电煮锅',159.00,'dianzhuguo3.jpg','分体式电煮锅，方便清洗，学生宿舍使用','小家电',0,2,0,NULL),(76,'家用空气炸锅',299.00,'kongqizhaguo.jpg','家用大容量空气炸锅，无油健康，学生宿舍/家用都适用','小家电',1,2,0,NULL),(77,'电热水壶',89.00,'shaoshuihu.jpg','家用保温电热水壶，快速烧水，宿舍/家用都适用','小家电',0,2,2,NULL),(78,'家用微波炉',399.00,'weibolu1.jpg','家用小型微波炉，加热快速，学生宿舍/租房使用','小家电',0,2,0,NULL),(79,'立式饮水机',299.00,'yinshuiji.jpg','立式冷热饮水机，适合宿舍、办公室使用','小家电',0,4,0,NULL),(80,'标准篮球',89.00,'basketball1.jpg','标准7号篮球，耐磨防滑，学生校园篮球必备','运动器材',1,4,0,NULL),(81,'训练篮球',79.00,'basketball2.jpg','室内外训练篮球，手感舒适，适合学生日常练习','运动器材',0,4,0,NULL),(82,'标准足球',79.00,'football.jpg','标准5号足球，耐磨耐用，学生校园足球必备','运动器材',0,4,0,NULL),(83,'哑铃',99.00,'huling.jpg','家用健身哑铃，可调节重量，学生宿舍健身神器','运动器材',0,3,0,NULL),(84,'乒乓球拍套装',129.00,'pingpangqiupai.jpg','学生乒乓球拍套装，手感舒适，校园运动必备','运动器材',0,3,0,NULL),(85,'训练网球',39.00,'wangqiu.jpg','训练用网球，耐磨耐用，适合学生日常练习','运动器材',0,3,1,2),(86,'网球拍',299.00,'wangqiupai1.jpg','专业网球拍，适合学生入门、练习','运动器材',1,3,0,NULL),(87,'儿童网球拍',199.00,'wangqiupai2.jpg','儿童/学生入门网球拍，轻便易操作','运动器材',0,3,0,NULL),(88,'羽毛球拍',199.00,'yumaoqiupai1.jpg','专业羽毛球拍，手感轻盈，学生校园运动必备','运动器材',0,1,0,NULL),(89,'羽毛球拍套装',249.00,'yumaoqiupai2.jpg','羽毛球拍套装，含拍包，学生入门首选','运动器材',0,1,0,NULL),(90,'创意摆件',59.00,'baijian1.jpg','创意家居摆件，提升生活质感，学生宿舍装饰','其他',1,1,0,NULL),(91,'大富翁桌游',49.00,'dafuweng.jpg','经典大富翁桌游，学生宿舍聚会神器','其他',0,1,0,NULL),(92,'持妆粉底液',129.00,'fendiye.jpg','持妆粉底液，遮瑕保湿，学生日常化妆必备','其他',0,3,0,NULL),(93,'无痕挂钩',19.90,'guagou.jpg','无痕免打孔挂钩，承重强，学生宿舍收纳神器','其他',0,3,0,NULL),(94,'装饰画',79.00,'hua1.jpg','简约装饰画，提升宿舍氛围感，学生宿舍装饰','其他',0,3,0,NULL),(95,'化妆刷套装',59.00,'huazhuangshua.jpg','学生化妆刷套装，柔软不扎脸，新手必备','其他',1,4,1,2),(96,'健身房次卡',99.00,'jianshenka.jpg','校园健身房次卡，学生健身必备','其他',0,4,0,NULL),(97,'哑光口红',89.00,'kouhong1.jpg','哑光显白口红，学生日常化妆必备','其他',0,4,0,NULL),(98,'三国杀桌游',69.00,'sanguosha.jpg','经典三国杀桌游，学生宿舍聚会神器','其他',0,2,0,NULL),(99,'创意沙漏',49.00,'shalou.jpg','创意时间沙漏，装饰、计时两用，学生宿舍摆件','其他',0,2,0,NULL),(100,'电动车',100.00,'diandongche2.jpg','很新','交通出行',0,2,0,NULL),(101,'中性笔套装',9.90,'pen9.jpg','全新未拆封，书写流畅','学习用品',1,1,0,NULL),(102,'现代汉语词典',39.90,'zidian.jpg','九成新，无破损，学生必备','学习用品',0,1,0,NULL),(103,'考研真题资料合集',29.90,'menpiao9.jpg','公共课+专业课真题，笔记清晰','学习用品',0,1,0,NULL),(104,'USB暖手宝',19.90,'nuanshoubao1.jpg','冬天必备，小巧便携','学习用品',0,4,0,NULL),(105,'影视会员年卡',99.00,'vip9.jpg','直充账号，追剧学习两不误','学习用品',1,4,0,NULL),(106,'大容量收纳包',29.90,'bidai.jpg','可装化妆品、文具、数据线','生活用品',0,4,0,NULL),(107,'玻璃泡茶壶',39.90,'chahu9.jpg','耐高温，泡茶专用','生活用品',0,4,0,NULL),(108,'桌面收纳桶',19.90,'lanzi9.jpg','宿舍桌面收纳神器','生活用品',0,2,0,NULL),(109,'24寸行李箱',199.00,'xinglixiang9.jpg','静音轮，结实耐用','生活用品',0,2,0,NULL),(110,'免手洗懒人拖把',39.90,'tuoba9.jpg','打扫卫生超方便','生活用品',1,2,0,NULL),(111,'折叠学习桌',129.00,'zhuozi9.jpg','床上可用，稳固不晃','生活用品',0,2,0,NULL),(112,'防滑衣架套装',19.90,'yijia9.jpg','家用宿舍通用，不易变形','生活用品',0,2,0,NULL),(113,'耐克双肩背包',199.00,'bag9.jpg','正品九成新，容量大','生活用品',0,2,0,NULL),(114,'苹果iMac一体机',4999.00,'iMac1.jpg','办公学习神器，成色很新','电子数码',0,1,0,NULL),(115,'iPad 8代平板电脑',1999.00,'ipad8.jpg','可画画记笔记，功能完好','电子数码',0,3,0,NULL),(116,'iPad 9代平板电脑',2199.00,'ipad9.jpg','电池健康度高，无维修','电子数码',0,3,0,NULL),(117,'MacBook Pro笔记本',4999.00,'laptop1.jpg','设计编程办公都能用','电子数码',0,3,0,NULL),(118,'联想拯救者游戏本',3999.00,'laptop2.jpg','性能强劲，畅玩主流游戏','电子数码',0,3,0,NULL),(119,'智能计数跳绳',59.90,'tiaosheng9.jpg','运动健身，可连蓝牙','电子数码',1,3,0,NULL),(120,'休闲西装外套',199.00,'cloth8.jpg','毕业面试穿搭，版型正','服饰',0,3,0,NULL),(121,'运动连帽外套',99.00,'cloth9.jpg','春秋季百搭，舒适透气','服饰',0,3,0,NULL),(122,'格纹收腰连衣裙',129.00,'dress9.jpg','显瘦显高，拍照好看','服饰',0,3,0,NULL),(123,'商务条纹领带',39.00,'lingdai9.jpg','正装搭配，质感好','服饰',0,4,0,NULL),(124,'专业双翘滑板',199.00,'huaban9.jpg','刷街代步，轮子顺滑','运动器材',0,4,0,NULL),(125,'专业竞速跳绳',29.90,'tiaosheng8.jpg','健身减肥必备','运动器材',0,4,0,NULL),(126,'家用健身哑铃单只',49.90,'yaling8.jpg','力量训练，重量合适','运动器材',0,4,0,NULL),(127,'可调节哑铃套装',99.00,'yaling9.jpg','宿舍健身全套装备','运动器材',0,1,0,NULL),(128,'加厚防滑瑜伽垫',59.90,'yujiadian9.jpg','静音减震，家用健身','运动器材',0,1,0,NULL),(129,'儿童成人平衡车',599.00,'pinghengche9.jpg','代步出行，续航久','运动器材',0,2,0,NULL),(130,'静音落地风扇',199.00,'fengshan9.jpg','三档风速，夏天必备','小家电',0,2,0,NULL),(131,'家用超滤净水器',299.00,'jiashiqi9.jpg','直饮净水，健康生活','小家电',0,2,0,NULL),(132,'大容量水杯',99.00,'shuiping9.jpg','大容量，一天都喝不完','生活用品',0,2,0,NULL),(133,'迷你小型洗衣机',399.00,'xiyiji9.jpg','洗内衣袜子，宿舍可用','小家电',0,1,0,NULL),(134,'复古巡航摩托车',9999.00,'motuo6.jpg','车况精品，手续齐全','交通出行',0,3,0,NULL),(135,'仿赛跑车摩托车',12999.00,'motuo7.jpg','声浪好听，颜值超高','交通出行',1,3,0,NULL),(136,'电动轻便摩托车',2999.00,'motuo8.jpg','通勤代步，续航长','交通出行',0,3,0,NULL),(137,'街车代步摩托车',8999.00,'motuo9.jpg','城市通勤神器','交通出行',0,3,0,NULL),(138,'变速公路自行车',899.00,'zixingche9.jpg','骑行轻快，颜值高','交通出行',0,3,0,NULL),(139,'111',1111.00,'zmt.jpg','111','生活用品',0,2,2,NULL),(140,'二手相机',600.00,'xiangji2.jpg','买来拍了几次，就没用了','电子数码',0,4,0,NULL),(141,'二手篮球',60.00,'lanqiu6.jpg','还可以','运动器材',0,4,0,NULL),(142,'二手拼豆',50.00,'微信图片_20260401113835_325_86.png','我自己拼了4个小时，原创且独一份，哈哈哈','其他',0,2,0,NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_click`
--

DROP TABLE IF EXISTS `product_click`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_click` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `click_count` int(11) DEFAULT '1' COMMENT '点击次数',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_click`
--

LOCK TABLES `product_click` WRITE;
/*!40000 ALTER TABLE `product_click` DISABLE KEYS */;
INSERT INTO `product_click` VALUES (36,4,47,1,'2026-05-08 08:44:40'),(42,4,34,1,'2026-05-13 15:22:25'),(43,4,119,1,'2026-05-13 15:22:55'),(44,4,41,1,'2026-05-13 15:40:06'),(47,2,141,1,'2026-06-09 12:57:34'),(48,2,16,1,'2026-06-12 08:39:29'),(49,2,22,2,'2026-06-12 16:47:46'),(50,2,6,1,'2026-06-12 08:46:58'),(51,2,63,1,'2026-08-11 13:45:37'),(52,2,86,1,'2026-08-11 13:48:30');
/*!40000 ALTER TABLE `product_click` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_log`
--

DROP TABLE IF EXISTS `transaction_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `pay_type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `transaction_log_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `transaction_log_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_log`
--

LOCK TABLES `transaction_log` WRITE;
/*!40000 ALTER TABLE `transaction_log` DISABLE KEYS */;
INSERT INTO `transaction_log` VALUES (132,NULL,2,51.00,'income','recharge','success','2026-04-11 08:29:58'),(133,NULL,2,1000.00,'income','recharge','success','2026-04-11 09:37:27'),(134,NULL,1,100.00,'income','recharge','success','2026-04-13 02:15:21'),(135,68,2,19.90,'pay','wallet','success','2026-04-13 02:24:08'),(136,68,3,19.90,'income','wallet','success','2026-04-13 02:24:08'),(137,69,2,199.90,'pay','wallet','success','2026-04-13 02:24:32'),(138,69,4,199.90,'income','wallet','success','2026-04-13 02:24:32'),(139,70,2,299.00,'pay','wallet','success','2026-04-13 02:24:43'),(140,70,4,299.00,'income','wallet','success','2026-04-13 02:24:43'),(141,72,2,19.90,'pay','wallet','success','2026-04-13 02:39:19'),(142,72,4,19.90,'income','wallet','success','2026-04-13 02:39:19'),(143,72,2,19.90,'refund','wallet','success','2026-04-13 02:45:45'),(144,72,4,19.90,'deduct','wallet','success','2026-04-13 02:45:45'),(145,73,3,90.60,'pay','wallet','success','2026-04-19 11:41:43'),(146,73,2,90.60,'income','wallet','success','2026-04-19 11:41:43'),(147,74,3,129.90,'pay','wallet','success','2026-04-19 13:48:58'),(148,74,2,129.90,'income','wallet','success','2026-04-19 13:48:58'),(149,75,3,19.90,'pay','wallet','success','2026-04-19 13:53:15'),(150,75,4,19.90,'income','wallet','success','2026-04-19 13:53:15'),(151,75,3,19.90,'refund','wallet','success','2026-04-19 13:54:02'),(152,75,4,19.90,'deduct','wallet','success','2026-04-19 13:54:02'),(153,NULL,2,10000.00,'income','recharge','success','2026-04-21 09:30:27'),(154,70,2,299.00,'refund','wallet','success','2026-05-03 08:24:46'),(155,70,4,299.00,'deduct','wallet','success','2026-05-03 08:24:46'),(156,76,2,5.90,'pay','wallet','success','2026-05-03 08:43:44'),(157,76,4,5.90,'income','wallet','success','2026-05-03 08:43:44'),(158,NULL,2,1000.00,'income','recharge','success','2026-05-03 12:21:49'),(159,78,2,199.00,'pay','wallet','success','2026-05-06 08:28:18'),(160,78,4,199.00,'income','wallet','success','2026-05-06 08:28:18'),(161,79,2,549.00,'pay','wallet','success','2026-05-06 08:28:37'),(162,79,1,549.00,'income','wallet','success','2026-05-06 08:28:37'),(163,78,2,199.00,'refund','wallet','success','2026-05-06 08:31:56'),(164,78,4,199.00,'deduct','wallet','success','2026-05-06 08:31:56'),(165,79,2,549.00,'refund','wallet','success','2026-05-06 08:32:00'),(166,79,1,549.00,'deduct','wallet','success','2026-05-06 08:32:00'),(167,80,2,39.00,'pay','wallet','success','2026-05-06 09:16:58'),(168,80,3,39.00,'income','wallet','success','2026-05-06 09:16:58'),(169,81,2,19.90,'pay','wallet','success','2026-05-06 09:23:12'),(170,81,4,19.90,'income','wallet','success','2026-05-06 09:23:12'),(171,82,2,59.00,'pay','wallet','success','2026-05-06 13:06:27'),(172,82,4,59.00,'income','wallet','success','2026-05-06 13:06:27'),(173,83,4,99.90,'pay','wallet','success','2026-05-08 08:44:43'),(174,83,3,99.90,'income','wallet','success','2026-05-08 08:44:43'),(175,NULL,4,1000.00,'income','recharge','success','2026-05-13 15:21:47'),(176,84,2,15.90,'refund','offline','success','2026-05-20 11:52:09'),(177,84,4,15.90,'deduct','offline','success','2026-05-20 11:52:09'),(178,85,2,15.90,'pay','wallet','success','2026-06-12 08:48:03'),(179,85,4,15.90,'income','wallet','success','2026-06-12 08:48:03'),(180,86,2,899.00,'pay','wallet','success','2026-08-11 13:46:54'),(181,86,4,899.00,'income','wallet','success','2026-08-11 13:46:54'),(182,NULL,2,1000.00,'income','recharge','success','2026-08-11 13:50:32');
/*!40000 ALTER TABLE `transaction_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `student_id` varchar(30) NOT NULL COMMENT '学号',
  `card_id` varchar(30) NOT NULL COMMENT '校园一卡通号（注册/找回密码用）',
  `phone` varchar(255) DEFAULT NULL,
  `university` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `credit_score` int(11) DEFAULT '100' COMMENT '信用分',
  `credit_level` varchar(255) DEFAULT NULL,
  `default_address_id` int(11) DEFAULT NULL COMMENT '默认地址ID',
  `description` varchar(255) DEFAULT NULL COMMENT '个人描述',
  `user_tags` varchar(255) DEFAULT '',
  `gender` varchar(255) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  UNIQUE KEY `uk_card_id` (`card_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='校园用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Away','112560','20260001','20260001','13800001111','四川大学','away.jpg',64,'良好',NULL,'冰红茶他爸','','男','2004-11-26'),(2,'huyiming','112588','2023112588','20260002','13899767523','西南交通大学','hym.jpg',72,'良好',3,'大三在校生，','','男','2004-11-26'),(3,'zhaomeiting','112618','2023112618','20260003','13700003333','西南交通大学','zmt.jpg',82,'优秀',NULL,'绝世好物，想要的后台滴滴我，更多好物在我主页','','女','2003-12-18'),(4,'xuya','112580','2023112580','20260004','13600005555','西南交通大学','xy.jpg',106,'极好',NULL,'谁要我的商品','','女','2002-07-21'),(7,'admin','admin','999999','999999','999999','斯坦福大学','2.jpg',999,'极好',NULL,'我是管理员','','未设置','2112-06-08'),(8,'Peter','123456','2025112588','20250009','13699980002','四川大学','3.jpg',60,'良好',NULL,NULL,'','男',NULL),(9,'Lisa','123456','2022112696','20220001','10010','四川大学','1777273172294_微信图片_20260401113835_325_86.png',60,'良好',NULL,'这个人很懒，什么都没留下~',NULL,'男',NULL),(10,'Jay','123456','20260100','20260100','13689756589','四川大学','1777273189836_admin.jpg',60,'良好',NULL,'这个人很懒，什么都没留下~',NULL,'男','2003-03-10'),(11,'1111','1111','1111','1111','1111','电子科大','default.jpg',60,'良好',NULL,'这个人很懒，什么都没留下~',NULL,'男','2025-12-12'),(12,'222','222','222','222','222','电子科大','default.jpg',60,'良好',NULL,'这个人很懒，什么都没留下~',NULL,'女','2025-10-10');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_coupon`
--

DROP TABLE IF EXISTS `user_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `coupon_id` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `get_time` datetime DEFAULT NULL,
  `use_time` datetime DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `coupon_id` (`coupon_id`),
  CONSTRAINT `user_coupon_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_coupon_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_coupon`
--

LOCK TABLES `user_coupon` WRITE;
/*!40000 ALTER TABLE `user_coupon` DISABLE KEYS */;
INSERT INTO `user_coupon` VALUES (5,3,4,'used','2026-04-08 15:08:40','2026-04-09 03:39:59','2026-05-08 15:08:40'),(6,3,2,'used','2026-04-08 15:14:21','2026-04-09 03:42:35','2026-05-08 15:14:21'),(145,4,1,'unused','2026-04-14 09:57:48',NULL,'2026-05-14 09:57:48'),(146,4,2,'unused','2026-04-14 09:57:53',NULL,'2026-05-14 09:57:53'),(147,2,1,'unused','2026-04-20 02:18:57',NULL,'2026-05-20 02:18:57'),(148,2,4,'unused','2026-04-20 02:19:03',NULL,'2026-05-20 02:19:03'),(149,2,1,'unused','2026-05-03 08:48:32',NULL,'2026-06-02 08:48:32');
/*!40000 ALTER TABLE `user_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `balance` decimal(38,2) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `wallet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
INSERT INTO `wallet` VALUES (1,2,31978.00,NULL,'2026-08-11 21:50:32'),(2,3,8768.41,NULL,'2026-05-08 16:44:43'),(3,1,3135.90,NULL,'2026-05-06 16:28:37'),(5,4,7083.80,NULL,'2026-08-11 21:46:54'),(7,8,30000.00,NULL,NULL),(8,9,30000.00,NULL,NULL),(9,10,2000.00,'2026-04-10 14:23:25','2026-04-10 14:23:25');
/*!40000 ALTER TABLE `wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'campus_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-08 19:41:21
