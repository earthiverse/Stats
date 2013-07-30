CREATE TABLE `stats` (
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Username',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `statistic` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `data` smallint(6) NOT NULL,
  `value` bigint(20) NOT NULL,
  PRIMARY KEY (`username`,`category`,`statistic`,`data`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1