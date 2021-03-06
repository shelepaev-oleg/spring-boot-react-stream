DROP SEQUENCE IF EXISTS SEQ_TEST_FILE;
CREATE SEQUENCE SEQ_TEST_FILE
MINVALUE 1
MAXVALUE 99999999999999
START WITH 1
INCREMENT BY 1
NOCACHE
;

DROP TABLE IF EXISTS TEST_FILE;
CREATE TABLE TEST_FILE (
  id number(19) not null,
  bytes blob,
  name nvarchar2(255)
);
