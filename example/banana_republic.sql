CREATE TABLE `banana_republic` (
  `id` int(10) unsigned NOT NULL,
  `dictator_name` varchar(64) default NULL,
  `dictator_height` int(11) default NULL,
  `gross_domestic_product` float default NULL,
  `primary_export` varchar(64) default 'bananas',
  `us_puppet` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Winter Code Generator Example'