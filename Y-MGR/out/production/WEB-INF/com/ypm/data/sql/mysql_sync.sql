CREATE TABLE IF NOT EXISTS {0} (
  `Sid` bigint(20) NOT NULL AUTO_INCREMENT,
  `Cls` int(10) NOT NULL DEFAULT '0',
  `Rev` int(10) NOT NULL DEFAULT '0',
  `Content` text CHARACTER SET utf8mb4,
  `FPath` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `Time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;