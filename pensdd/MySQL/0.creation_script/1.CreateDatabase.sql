create database pens;

GRANT USAGE ON *.* TO `pens`@`%` REQUIRE NONE;
GRANT Delete  ON `pens`.* TO `pens`@`%`;
GRANT Create View  ON `pens`.* TO `pens`@`%`;
GRANT Index  ON `pens`.* TO `pens`@`%`;
GRANT Select  ON `pens`.* TO `pens`@`%`;
GRANT USAGE  ON `pens`.* TO `pens`@`%` WITH GRANT OPTION;
GRANT References  ON `pens`.* TO `pens`@`%`;
GRANT Execute  ON `pens`.* TO `pens`@`%`;
GRANT Create  ON `pens`.* TO `pens`@`%`;
GRANT Alter routine  ON `pens`.* TO `pens`@`%`;
GRANT Lock tables  ON `pens`.* TO `pens`@`%`;
GRANT Show view  ON `pens`.* TO `pens`@`%`;
GRANT Update  ON `pens`.* TO `pens`@`%`;
GRANT Drop  ON `pens`.* TO `pens`@`%`;
GRANT Insert  ON `pens`.* TO `pens`@`%`;
GRANT Create routine  ON `pens`.* TO `pens`@`%`;
GRANT Alter  ON `pens`.* TO `pens`@`%`;
GRANT Create temporary tables  ON `pens`.* TO `pens`@`%`;

GRANT USAGE ON *.* TO `pens`@`localhost` REQUIRE NONE;
GRANT Drop  ON `pens`.* TO `pens`@`localhost`;
GRANT References  ON `pens`.* TO `pens`@`localhost`;
GRANT Alter routine  ON `pens`.* TO `pens`@`localhost`;
GRANT Create  ON `pens`.* TO `pens`@`localhost`;
GRANT Create routine  ON `pens`.* TO `pens`@`localhost`;
GRANT USAGE  ON `pens`.* TO `pens`@`localhost` WITH GRANT OPTION;
GRANT Delete  ON `pens`.* TO `pens`@`localhost`;
GRANT Execute  ON `pens`.* TO `pens`@`localhost`;
GRANT Alter  ON `pens`.* TO `pens`@`localhost`;
GRANT Insert  ON `pens`.* TO `pens`@`localhost`;
GRANT Select  ON `pens`.* TO `pens`@`localhost`;
GRANT Index  ON `pens`.* TO `pens`@`localhost`;
GRANT Lock tables  ON `pens`.* TO `pens`@`localhost`;
GRANT Create temporary tables  ON `pens`.* TO `pens`@`localhost`;
GRANT Update  ON `pens`.* TO `pens`@`localhost`;
GRANT Show view  ON `pens`.* TO `pens`@`localhost`;
GRANT Create View  ON `pens`.* TO `pens`@`localhost`;


ALTER DATABASE pens
DEFAULT CHARACTER SET tis620 COLLATE tis620_thai_ci;


