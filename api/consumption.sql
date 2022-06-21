/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : money

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2021-07-29 22:54:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for consumption
-- ----------------------------
DROP TABLE IF EXISTS `consumption`;
CREATE TABLE `consumption` (
  `con_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `con_userId` int(11) DEFAULT NULL COMMENT '用户id',
  `con_dissipate` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `con_date` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '记录消费日期',
  `con_money` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '消费金额',
  `con_record` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '消费分类',
  `con_remarks` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `con_consume` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '收入还是支出',
  `con_refund` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否退款',
  `con_refundMoney` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '退款金额',
  `con_refundTime` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '退款时间',
  `con_refundRemarks` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '退款备注',
  PRIMARY KEY (`con_id`)
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
