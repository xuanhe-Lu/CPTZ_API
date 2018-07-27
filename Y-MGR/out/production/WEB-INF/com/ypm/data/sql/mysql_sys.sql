SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_info` (
  `UserId` bigint(20) NOT NULL,
  `UserNo` varchar(20) DEFAULT NULL,
  `UserName` varchar(50) DEFAULT NULL,
  `PassWord` varchar(32) DEFAULT NULL,
  `RealName` varchar(50) DEFAULT NULL,
  `Menu` varchar(10) DEFAULT NULL,
  `Dept` varchar(10) DEFAULT NULL,
  `Job` int(10) NOT NULL DEFAULT '0',
  `Org` int(10) NOT NULL DEFAULT '0',
  `State` tinyint(1) NOT NULL DEFAULT '0',
  `Soger` tinyint(1) NOT NULL DEFAULT '0',
  `Super` tinyint(1) NOT NULL DEFAULT '0',
  `Time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`UserId`), UNIQUE KEY `UserName` (`UserName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_dept` (
  `Id` varchar(10) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Remark` varchar(1000) DEFAULT NULL,
  `Header` varchar(500) DEFAULT NULL,
  `Heades` varchar(1500) DEFAULT NULL,
  `Viewer` varchar(1000) DEFAULT NULL,
  `Viewes` varchar(1500) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_jobs` (
  `Code` smallint(5) NOT NULL,
  `Type` smallint(5) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  `Remark` varchar(1000) DEFAULT NULL,
  `Leaf` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_orgs` (
  `Code` smallint(5) NOT NULL,
  `Type` smallint(5) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  `Remark` varchar(1000) DEFAULT NULL,
  `Leaf` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- --------------------------------------------------------

-- --------------------------------------------------------
-- --------------------------------------------------------
-- --------------------------------------------------------
-- --------------------------------------------------------
-- --------------------------------------------------------
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comm_config` (
  `Id` varchar(50) NOT NULL,
  `Type` tinyint(3) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Context` text,
  `Remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comm_icon` (
  `Sid` varchar(50) NOT NULL,
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Sid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comm_jobs` (
  `Id` varchar(15) NOT NULL,
  `Sortid` int(10) NOT NULL DEFAULT '0',
  `Zone` int(10) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  `Sday` varchar(20) DEFAULT NULL,
  `Eday` varchar(20) DEFAULT NULL,
  `Outs` text,
  `Times` varchar(500) DEFAULT NULL,
  `Weeks` varchar(10) DEFAULT NULL,
  `Stype` tinyint(3) NOT NULL DEFAULT '0',
  `Script` text,
  `Rlast` bigint(20) NOT NULL DEFAULT '0',
  `Rnext` bigint(20) NOT NULL DEFAULT '0',
  `Rtime` bigint(20) NOT NULL DEFAULT '0',
  `Runs` tinyint(1) NOT NULL DEFAULT '0',
  `State` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`), KEY `Rnext` (`Rnext`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comm_menu` (
  `Id` int(10) NOT NULL,
  `Tid` int(10) NOT NULL,
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  `Module` varchar(200) DEFAULT NULL,
  `Remark` varchar(500) DEFAULT NULL,
  `Icon` varchar(50) DEFAULT NULL,
  `Leaf` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`), KEY `Tid` (`Tid`,`Sortid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dict_menu` (
  `Sid` int(10) NOT NULL,
  `Tid` int(10) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `SNo` varchar(50) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Type` tinyint(1) NOT NULL DEFAULT '0',
  `Leaf` tinyint(1) NOT NULL DEFAULT '0',
  `Losk` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Sid`), UNIQUE KEY `SNo` (`SNo`), FULLTEXT KEY `Name` (`Name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dict_defs` (
  `Id` varchar(10) NOT NULL,
  `Sid` int(10) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(100) DEFAULT NULL,
  `Value` text,
  `Remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`Sid`,`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dict_info` (
  `Id` int(10) NOT NULL,
  `Sid` int(10) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(100) DEFAULT NULL,
  `Value` varchar(500) DEFAULT NULL,
  `Remark` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Id`), KEY `Sid` (`Sid`,`Sortid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dict_user` (
  `Id` int(10) NOT NULL,
  `Sid` int(10) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(100) DEFAULT NULL,
  `Value` varchar(500) DEFAULT NULL,
  `Remark` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Id`), KEY `Sid` (`Sid`,`Sortid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `field_menu` (
  `Sid` int(10) NOT NULL,
  `Tid` int(10) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `SNo` varchar(50) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Title` varchar(100) DEFAULT NULL,
  `Value` varchar(50) DEFAULT NULL,
  `Leaf` tinyint(1) NOT NULL DEFAULT '0',
  `Losk` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Sid`), UNIQUE KEY `SNo` (`SNo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `field_info` (
  `Id` int(10) NOT NULL,
  `Sid` int(10) NOT NULL DEFAULT '0',
  `Sdef` smallint(5) NOT NULL DEFAULT '0',
  `Sortid` smallint(5) NOT NULL DEFAULT '0',
  `Name` varchar(50) DEFAULT NULL,
  `Nice` varchar(50) DEFAULT NULL,
  `Width` int(10) NOT NULL DEFAULT '0',
  `Pkey` tinyint(1) NOT NULL DEFAULT '0',
  `Display` tinyint(1) NOT NULL DEFAULT '0',
  `Sortab` tinyint(1) DEFAULT '0',
  `Export` tinyint(1) NOT NULL DEFAULT '0',
  `Type` tinyint(1) NOT NULL DEFAULT '0',
  `Format` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Id`), KEY `Sid` (`Sid`,`Sortid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

