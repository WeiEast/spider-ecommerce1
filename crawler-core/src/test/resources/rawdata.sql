DROP DATABASE `rawdata`;
CREATE DATABASE `rawdata` DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
CREATE TABLE `rawdata`.`T_WEBSITE_CONF` (
    `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `WebsiteId` int(10) unsigned NOT NULL,
	`SearchConfig` text COMMENT 'search config',
    `ExtractorConfig` text COMMENT 'extractor config',
	`CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`Id`),
	UNIQUE KEY `uq_WebsiteId` (`WebsiteId`)
)ENGINE=InnoDB  COMMENT='website config,sopport search';


CREATE TABLE `rawdata`.`T_WEBSITE` (
    `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `WebsiteType` enum('mail','operator','ecommerce','bank') NOT NULL DEFAULT 'mail',
    `WebsiteName` varchar(25) NOT NULL COMMENT 'website name',
    `WebsiteDomain` varchar(25) NOT NULL COMMENT 'website domain',
    `IsEnabled` enum('true','false') NOT NULL DEFAULT 'false',
	`CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`Id`),
	UNIQUE KEY `uq_WebsiteName` (`WebsiteName`),
    UNIQUE KEY `uq_WebsiteDomain` (`WebsiteDomain`)
)ENGINE=InnoDB  COMMENT='website basic info';

CREATE TABLE `rawdata`.`T_BANK` (
  `Id` smallint(4) unsigned NOT NULL AUTO_INCREMENT COMMENT 'bank Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL  COMMENT 'not null if bank support search',
  `BankName` varchar(25) NOT NULL COMMENT 'bank name',
  `BankEmailAddr` varchar(100) DEFAULT NULL,
  `IsEnabled` enum('true','false') NOT NULL DEFAULT 'false',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  COMMENT='Bank basic info';

CREATE TABLE `rawdata`.`T_OPERATOR` (
  `Id` smallint(4) unsigned NOT NULL AUTO_INCREMENT COMMENT 'operator Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL COMMENT 'not null if operator support search',
  `OperatorName` varchar(25) NOT NULL COMMENT 'operator name',
  `region` varchar(100) NOT NULL COMMENT 'operator region',
  `IsEnabled` enum('true','false') NOT NULL DEFAULT 'false',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  COMMENT='operator basic info';


CREATE TABLE `rawdata`.`T_ECOMMERCE` (
  `Id` smallint(4) unsigned NOT NULL AUTO_INCREMENT COMMENT 'bank Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL COMMENT 'not null if ecommerce support search',
  `EcommerceName` varchar(25) NOT NULL COMMENT 'ecommerce name',
  `IsEnabled` enum('true','false') NOT NULL DEFAULT 'false',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  COMMENT='ecommerce basic info';



/*CREATE TABLE `rawdata`.`T_EXTRACT_CONF` (
    `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PhaseType` enum('mailbill','operatorbill','ecommercebill','ebankbill') NOT NULL DEFAULT 'mailbill',
    `PhaseId` int(10) unsigned DEFAULT NULL COMMENT 'the id of T_ECOMMERCE or T_OPERATOR or T_BANK',
    `Config` text COMMENT 'extract config',
    `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`Id`),
    UNIQUE KEY `uq_PhaseType_PhaseId` (`PhaseType`,`PhaseId`)
)ENGINE=InnoDB  COMMENT='bill extract config';*/




CREATE TABLE `rawdata`.`T_MAIL_EXTRACT_RESULT` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'mail extract result Id',
  `UserId` int(10) unsigned NOT NULL COMMENT 'user Id',
  `TaskId` int(10) unsigned NOT NULL COMMENT 'task log Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL  COMMENT 'mail website id',
  `UniqueSign` char(32) NOT NULL COMMENT 'the unique sign for mail in diff website',
  `ResultType` tinyint(2) unsigned DEFAULT NULL  COMMENT '0:billmail',
  `Status` tinyint(2) unsigned DEFAULT 0 COMMENT '0:extract success，1:no extract conf，2：extract failed',
  `Remark` varchar(255) DEFAULT NULL COMMENT 'extract result Remark',
  `BankId` smallint(4)  unsigned  COMMENT 'bank Id',
  `Sender` varchar(255) COMMENT 'mail sender',
  `Subject` varchar(255) COMMENT 'mail subject,length may larger than 255 please sub(0,255)',
  `ReceiveAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `StoragePath` varchar(255) NOT NULL COMMENT 'storage Path',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  KEY `idx_UserId_ReceiveAt` (`UserId`,`ReceiveAt`),
  UNIQUE KEY `uq_WebsiteId_UniqueSign` (`WebsiteId`,`UniqueSign`)
) ENGINE=InnoDB  COMMENT='mail result info';


CREATE TABLE `rawdata`.`T_OPERATOR_EXTRACT_RESULT` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'operator extract result Id',
  `UserId` int(10) unsigned NOT NULL COMMENT 'user Id',
  `TaskId` int(10) unsigned NOT NULL COMMENT 'task log Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL  COMMENT 'operator website id',
  `OperatorId` int(10) unsigned DEFAULT NULL  COMMENT 'operator id for extract',
  `Status` tinyint(2) unsigned DEFAULT 0 COMMENT '0:extract success，1:no extract conf，2：extract failed',
  `Remark` varchar(255) DEFAULT NULL COMMENT 'extract result Remark',
  `Url` varchar(1024) NOT NULL COMMENT 'file source url',
  `UniqueSign` char(32) NOT NULL COMMENT 'the unique sign for operator data in diff website',
  `ResultType` tinyint(2) unsigned DEFAULT NULL  COMMENT '0:personalInformation, 1:BillDetail,2:ShortMessageDetail,3:CallDetail',
  `StoragePath` varchar(255) NOT NULL COMMENT 'storage Path',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `uq_WebsiteId_UniqueSign` (`WebsiteId`,`UniqueSign`)
) ENGINE=InnoDB  COMMENT='operator result info';


CREATE TABLE `rawdata`.`T_ECOMMERCE_EXTRACT_RESULT` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ecommerce extract result Id',
  `UserId` int(10) unsigned NOT NULL COMMENT 'user Id',
  `TaskId` int(10) unsigned NOT NULL COMMENT 'task log Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL  COMMENT 'ecommerce website id',
  `EcommerceId` int(10) unsigned DEFAULT NULL  COMMENT 'ecommerce id for extract',
  `Status` tinyint(2) unsigned DEFAULT 0 COMMENT '0:extract success，1:no extract conf，2：extract failed',
  `Remark` varchar(255) DEFAULT NULL COMMENT 'extract result Remark',
  `Url` varchar(1024) NOT NULL COMMENT 'file source url',
  `UniqueSign` char(32) NOT NULL COMMENT 'the unique sign for ecommerce data in diff website',
  `ResultType` tinyint(2) unsigned DEFAULT NULL  COMMENT '0:personalInformation, 1:BillDetail,2:ShortMessageDetail,3:CallDetail',
  `StoragePath` varchar(255) NOT NULL COMMENT 'storage Path',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `uq_WebsiteId_UniqueSign` (`WebsiteId`,`UniqueSign`)
) ENGINE=InnoDB  COMMENT='ecommerce result info';



CREATE TABLE `rawdata`.`T_TASKLOG` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'mail Id',
  `UserId` int(10) unsigned NOT NULL COMMENT 'user Id',
  `WebsiteId` int(10) unsigned DEFAULT NULL  COMMENT 'mail website id',
  `OpenUrlCount` int(10) unsigned NOT NULL DEFAULT '0',
  `OpenPageCount` int(10) unsigned NOT NULL DEFAULT '0',
  `RequestFailedCount` int(10) unsigned NOT NULL DEFAULT '0',
  `FilteredCount` int(10) unsigned NOT NULL DEFAULT '0',
  `Status` tinyint(3) unsigned DEFAULT 0 COMMENT '0:init;200:success;101:cookie Invalid，102：block 103：no result',
  `Remark` varchar(255) DEFAULT NULL,
  `Duration` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Task duration, in seconds',
  `ExtractedCount` int(10) unsigned NOT NULL DEFAULT '0',
  `ExtractSucceedCount` int(10) unsigned NOT NULL DEFAULT '0',
  `ExtractFailedCount` int(10) unsigned NOT NULL DEFAULT '0',
  `NotExtractCount` int(10) unsigned NOT NULL DEFAULT '0',
  `NetworkTraffic` bigint(20) unsigned NOT NULL DEFAULT '0',
  `StartedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `FinishedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
   KEY `idx_WebsiteId_CreatedAt` (`WebsiteId`,`CreatedAt`),
   KEY `idx_UserId` (`UserId`),
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  COMMENT='task log info';




CREATE TABLE `rawdata`.`T_KEYWORD` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Keyword` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT 'keyword',
  `IsEnabled` enum('true','false') NOT NULL DEFAULT 'false',
  `KeywordType` enum('mail','operator','ecommerce') NOT NULL DEFAULT 'mail',
  `CreatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  KEY `idx_CreatedAt_WebsiteId` (`KeywordType`,`Keyword`)
) ENGINE=InnoDB COMMENT='keyword';
