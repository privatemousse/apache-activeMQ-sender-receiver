/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : mq_test2

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2016-07-27 14:49:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for qmessage
-- ----------------------------
DROP TABLE IF EXISTS `qmessage`;
CREATE TABLE `qmessage` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT ,
  `message_id` varchar(127) COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `business_mark` varchar(127) COLLATE utf8_general_ci DEFAULT '' ,
  `message_content` text COLLATE utf8_general_ci NOT NULL ,
  `status` int(11) unsigned NOT NULL DEFAULT '0' ,
  `destination` varchar(127) COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
  `dest_type` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `time_stamp` bigint(25) unsigned NOT NULL DEFAULT '0',
  `retry` int(11) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_message_id` (`message_id`) USING BTREE 
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
