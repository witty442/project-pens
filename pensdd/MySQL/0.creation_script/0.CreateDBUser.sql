GRANT USAGE ON *.* TO `pens`@`localhost` IDENTIFIED BY 'pens' REQUIRE NONE;
GRANT Select  ON *.* TO `pens`@`localhost`;
GRANT Insert  ON *.* TO `pens`@`localhost`;
GRANT Update  ON *.* TO `pens`@`localhost`;
GRANT Delete  ON *.* TO `pens`@`localhost`;
GRANT Create  ON *.* TO `pens`@`localhost`;
GRANT Drop  ON *.* TO `pens`@`localhost`;
GRANT Reload  ON *.* TO `pens`@`localhost`;
GRANT Shutdown  ON *.* TO `pens`@`localhost`;
GRANT Process  ON *.* TO `pens`@`localhost`;
GRANT File  ON *.* TO `pens`@`localhost`;
GRANT USAGE  ON *.* TO `pens`@`localhost` WITH GRANT OPTION;
GRANT References  ON *.* TO `pens`@`localhost`;
GRANT Index  ON *.* TO `pens`@`localhost`;
GRANT Alter  ON *.* TO `pens`@`localhost`;
GRANT Show databases  ON *.* TO `pens`@`localhost`;
GRANT Super  ON *.* TO `pens`@`localhost`;
GRANT Create temporary tables  ON *.* TO `pens`@`localhost`;
GRANT Lock tables  ON *.* TO `pens`@`localhost`;
GRANT Execute  ON *.* TO `pens`@`localhost`;
GRANT Replication slave  ON *.* TO `pens`@`localhost`;
GRANT Replication client  ON *.* TO `pens`@`localhost`;
GRANT Create View  ON *.* TO `pens`@`localhost`;
GRANT Show view  ON *.* TO `pens`@`localhost`;
GRANT Create routine  ON *.* TO `pens`@`localhost`;
GRANT Alter routine  ON *.* TO `pens`@`localhost`;
GRANT Create user  ON *.* TO `pens`@`localhost`;

GRANT USAGE ON *.* TO `pens`@`%` IDENTIFIED BY 'pens' REQUIRE NONE;
GRANT File  ON *.* TO `pens`@`%`;
GRANT Select  ON *.* TO `pens`@`%`;
GRANT Update  ON *.* TO `pens`@`%`;
GRANT Insert  ON *.* TO `pens`@`%`;
GRANT Delete  ON *.* TO `pens`@`%`;
GRANT Create  ON *.* TO `pens`@`%`;
GRANT Drop  ON *.* TO `pens`@`%`;
GRANT Reload  ON *.* TO `pens`@`%`;
GRANT Shutdown  ON *.* TO `pens`@`%`;
GRANT Process  ON *.* TO `pens`@`%`;
GRANT USAGE  ON *.* TO `pens`@`%` WITH GRANT OPTION;
GRANT References  ON *.* TO `pens`@`%`;
GRANT Index  ON *.* TO `pens`@`%`;
GRANT Alter  ON *.* TO `pens`@`%`;
GRANT Show databases  ON *.* TO `pens`@`%`;
GRANT Super  ON *.* TO `pens`@`%`;
GRANT Create temporary tables  ON *.* TO `pens`@`%`;
GRANT Lock tables  ON *.* TO `pens`@`%`;
GRANT Execute  ON *.* TO `pens`@`%`;
GRANT Replication slave  ON *.* TO `pens`@`%`;
GRANT Replication client  ON *.* TO `pens`@`%`;
GRANT Create View  ON *.* TO `pens`@`%`;
GRANT Show view  ON *.* TO `pens`@`%`;
GRANT Create routine  ON *.* TO `pens`@`%`;
GRANT Alter routine  ON *.* TO `pens`@`%`;
GRANT Create user  ON *.* TO `pens`@`%`;