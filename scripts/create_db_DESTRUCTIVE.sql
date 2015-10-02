SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
CREATE DATABASE IF NOT EXISTS `acfe` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `acfe`;

DROP TABLE IF EXISTS `areas`;
CREATE TABLE IF NOT EXISTS `areas` (
`id` int(11) NOT NULL,
  `title` varchar(40) NOT NULL,
  `region_id` int(11) NOT NULL,
  `boundary-string` mediumtext NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `area_facts`;
CREATE TABLE IF NOT EXISTS `area_facts` (
`id` int(11) NOT NULL,
  `area_id` int(11) NOT NULL,
  `fact_id` int(11) NOT NULL,
  `detail_text` varchar(1000) DEFAULT NULL,
  `detail_value` decimal(10,2) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `facts`;
CREATE TABLE IF NOT EXISTS `facts` (
`id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `fact_category_id` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `fact_categories`;
CREATE TABLE IF NOT EXISTS `fact_categories` (
`id` int(11) NOT NULL,
  `title` varchar(40) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `places`;
CREATE TABLE IF NOT EXISTS `places` (
`id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `lat` decimal(10,5) NOT NULL,
  `lng` decimal(10,5) NOT NULL,
  `place_category_id` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=177 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `place_categories`;
CREATE TABLE IF NOT EXISTS `place_categories` (
`id` int(11) NOT NULL,
  `title` varchar(40) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `place_facts`;
CREATE TABLE IF NOT EXISTS `place_facts` (
`id` int(11) NOT NULL,
  `place_id` int(11) NOT NULL,
  `fact_id` int(11) NOT NULL,
  `detail_text` varchar(1000) DEFAULT NULL,
  `detail_value` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=944 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `regions`;
CREATE TABLE IF NOT EXISTS `regions` (
`id` int(11) NOT NULL,
  `title` varchar(20) NOT NULL,
  `colour` varchar(7) NOT NULL DEFAULT '#00ff00'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


ALTER TABLE `areas`
 ADD PRIMARY KEY (`id`), ADD KEY `region_id` (`region_id`);

ALTER TABLE `area_facts`
 ADD PRIMARY KEY (`area_fact_id`), ADD UNIQUE KEY `area_id` (`area_id`,`fact_id`), ADD KEY `fact_id` (`fact_id`);

ALTER TABLE `facts`
 ADD PRIMARY KEY (`id`), ADD KEY `fact_category_id` (`fact_category_id`);

ALTER TABLE `fact_categories`
 ADD PRIMARY KEY (`id`);

ALTER TABLE `places`
 ADD PRIMARY KEY (`id`), ADD KEY `place_category_id` (`place_category_id`);

ALTER TABLE `place_categories`
 ADD PRIMARY KEY (`id`);

ALTER TABLE `place_facts`
 ADD PRIMARY KEY (`place_fact_id`), ADD UNIQUE KEY `place_id` (`place_id`,`fact_id`), ADD KEY `fact_id` (`fact_id`);

ALTER TABLE `regions`
 ADD PRIMARY KEY (`id`);


ALTER TABLE `areas`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=22;
ALTER TABLE `area_facts`
MODIFY `area_fact_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1001;
ALTER TABLE `facts`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=133;
ALTER TABLE `fact_categories`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=18;
ALTER TABLE `places`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=177;
ALTER TABLE `place_categories`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
ALTER TABLE `place_facts`
MODIFY `place_fact_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=944;
ALTER TABLE `regions`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;

ALTER TABLE `areas`
ADD CONSTRAINT `areas_ibfk_1` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`);

ALTER TABLE `area_facts`
ADD CONSTRAINT `area_facts_ibfk_1` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`id`),
ADD CONSTRAINT `area_facts_ibfk_2` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`);

ALTER TABLE `facts`
ADD CONSTRAINT `facts_ibfk_1` FOREIGN KEY (`fact_category_id`) REFERENCES `fact_categories` (`id`);

ALTER TABLE `places`
ADD CONSTRAINT `places_ibfk_1` FOREIGN KEY (`place_category_id`) REFERENCES `place_categories` (`id`);

ALTER TABLE `place_facts`
ADD CONSTRAINT `place_facts_ibfk_1` FOREIGN KEY (`place_id`) REFERENCES `places` (`id`),
ADD CONSTRAINT `place_facts_ibfk_2` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`id`);
