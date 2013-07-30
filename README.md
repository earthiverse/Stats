About Stats
===========
Stats plugin for Bukkit/Minecraft

Created for my personal server, but you can have it, too!

EXPECT LOTS OF CHANGES, THIS IS NOT COMPLETE.



Changelog
=========
0.2
---
  Reorganized git repo
  Slightly safer multithreading
  A few more block fixes
  Doesn't alter the blocks and wreck everything on the server

0.1
---
  Initial Release



Setup
=====

Bukkit
------
Copy the jar to the plugin folder, fill in plugins/Stats/config.yml

MySQL
-----
CREATE TABLE `stats` (
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Username',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `statistic` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `data` smallint(6) NOT NULL,
  `value` bigint(20) NOT NULL,
  PRIMARY KEY (`username`,`category`,`statistic`,`data`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1


License
=======
Creative Commons 3.0!
<a rel="license" href="http://creativecommons.org/licenses/by/3.0/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by/3.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>.
