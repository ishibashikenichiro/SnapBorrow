CREATE TABLE `action_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `device` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `action` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `action_logs_user_id_fk` (`user_id`),
  CONSTRAINT `action_logs_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `book_catalogs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_code` varchar(13) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `authors` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `publisher` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `publication_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `image` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_book_catalogs_on_book_code` (`book_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `books` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_code` varchar(13) COLLATE utf8_unicode_ci NOT NULL,
  `group_id` int(11) NOT NULL,
  `shelf_id` int(11) NOT NULL,
  `delete_date` datetime DEFAULT NULL,
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `device_ids` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `registration_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `device_ids_user_id_fk` (`user_id`),
  CONSTRAINT `device_ids_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `error_reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `detail` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `delete_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `invitation_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `mail` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `expire_at` datetime DEFAULT NULL,
  `url_hash` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `invitation_logs_group_id_fk` (`group_id`),
  CONSTRAINT `invitation_logs_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `lend_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `lend_date` datetime NOT NULL,
  `return_date` datetime DEFAULT NULL,
  `cancel_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `temporary` tinyint(1) DEFAULT '0',
  `book_code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `lend_logs_user_id_fk` (`user_id`),
  KEY `index_lend_logs_on_book_id_and_user_id` (`book_id`,`user_id`),
  CONSTRAINT `lend_logs_book_id_fk` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `lend_logs_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `managers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_managers_on_group_id_and_user_id` (`group_id`,`user_id`),
  KEY `managers_user_id_fk` (`user_id`),
  CONSTRAINT `managers_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  CONSTRAINT `managers_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `members_user_id_fk` (`user_id`),
  KEY `index_members_on_group_id_and_user_id` (`group_id`,`user_id`),
  CONSTRAINT `members_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  CONSTRAINT `members_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `notification_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `message` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  `is_notify` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `notification_logs_user_id_fk` (`user_id`),
  CONSTRAINT `notification_logs_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `password_resets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `url_hash` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `expire_at` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `roundtable_files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `roundtable_id` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keywords` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `filename` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `uploadtime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_roundtable_files_on_user_id` (`user_id`),
  KEY `index_roundtable_files_on_roundtable_id` (`roundtable_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `roundtables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `title` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `keywords` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `book_code` varchar(13) COLLATE utf8_unicode_ci DEFAULT NULL,
  `startdatetime` datetime DEFAULT NULL,
  `enddatetime` datetime DEFAULT NULL,
  `place` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_roundtables_on_group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `schema_migrations` (
  `version` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `shelves` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shelf_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `name` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `delete_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_shelves_on_shelf_id_and_group_id` (`shelf_id`,`group_id`),
  KEY `shelves_group_id_fk` (`group_id`),
  CONSTRAINT `shelves_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `temporary_lend_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `lend_date` datetime NOT NULL,
  `return_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `lenders` int(11) DEFAULT NULL,
  `possessors` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `temporary_lend_logs_user_id_fk` (`user_id`),
  KEY `temporary_lend_logs_group_id_fk` (`group_id`),
  CONSTRAINT `temporary_lend_logs_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  CONSTRAINT `temporary_lend_logs_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login_id` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL,
  `nickname` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mail` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password_digest` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `delete_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_users_on_login_id` (`login_id`),
  UNIQUE KEY `index_users_on_mail` (`mail`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO schema_migrations (version) VALUES ('20140609033222');

INSERT INTO schema_migrations (version) VALUES ('20140609035418');

INSERT INTO schema_migrations (version) VALUES ('20140609035437');

INSERT INTO schema_migrations (version) VALUES ('20140609035440');

INSERT INTO schema_migrations (version) VALUES ('20140609035443');

INSERT INTO schema_migrations (version) VALUES ('20140609035539');

INSERT INTO schema_migrations (version) VALUES ('20140609035543');

INSERT INTO schema_migrations (version) VALUES ('20140609044701');

INSERT INTO schema_migrations (version) VALUES ('20140830064806');

INSERT INTO schema_migrations (version) VALUES ('20141007052918');

INSERT INTO schema_migrations (version) VALUES ('20141009085614');

INSERT INTO schema_migrations (version) VALUES ('20141013065227');

INSERT INTO schema_migrations (version) VALUES ('20141019072000');

INSERT INTO schema_migrations (version) VALUES ('20141021041525');

INSERT INTO schema_migrations (version) VALUES ('20141021041539');

INSERT INTO schema_migrations (version) VALUES ('20141022004032');

INSERT INTO schema_migrations (version) VALUES ('20141026171828');

INSERT INTO schema_migrations (version) VALUES ('20141112042146');

INSERT INTO schema_migrations (version) VALUES ('20141128021518');

INSERT INTO schema_migrations (version) VALUES ('20141225065806');

INSERT INTO schema_migrations (version) VALUES ('20150104214758');

INSERT INTO schema_migrations (version) VALUES ('20150128042320');

INSERT INTO schema_migrations (version) VALUES ('20150203041646');

INSERT INTO schema_migrations (version) VALUES ('20150529044746');

INSERT INTO schema_migrations (version) VALUES ('20150529050119');

INSERT INTO schema_migrations (version) VALUES ('20150603024511');

INSERT INTO schema_migrations (version) VALUES ('20150605033900');