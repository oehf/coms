DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
  name character varying(255) NOT NULL,
  forename character varying(255) NOT NULL,
  birthdate date NOT NULL,
  street character varying(255) NOT NULL,
  zipcode integer NOT NULL,
  city character varying(255) NOT NULL,
  gender character varying(255) NOT NULL,
  emailaddress character varying(255) UNIQUE,
  password character varying(255) NOT NULL,
  id character varying(255) NOT NULL,
  privileges integer NOT NULL,
  active integer NOT NULL,
  CONSTRAINT id_pkey PRIMARY KEY (id),
  CONSTRAINT users_masterdata UNIQUE (name, forename, gender, birthdate)
);

DROP TABLE IF EXISTS passwordRequests CASCADE;

CREATE TABLE passwordRequests
(
  refid character varying(64) NOT NULL,
  id character varying(255) NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  emailaddress character varying(255) NOT NULL,
  CONSTRAINT passwordRequests_id_fkey FOREIGN KEY (id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

DROP TABLE IF EXISTS registerConflicts CASCADE;

CREATE TABLE registerConflicts
(
  id character varying(255) NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  CONSTRAINT passwordRequests_id_fkey FOREIGN KEY (id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

DROP TABLE IF EXISTS unclearedConsents CASCADE;

CREATE TABLE unclearedConsents
(
  id character varying(255) NOT NULL,
  filename character varying(64) NOT NULL,
  md5hash character varying(32) NOT NULL,
  participation integer NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  CONSTRAINT passwordRequests_id_fkey FOREIGN KEY (id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);


INSERT INTO users (name, forename, birthdate, gender, street, zipcode, city, emailaddress, password, id, privileges, active) 
VALUES ('admin', 'leer', 'Jan-10-1999', 'male', 'leer', '69120', 'leer', 'root', '202cb962ac59075b964b07152d234b70', '1234567890', '2', '1');

INSERT INTO users (name, forename, birthdate, gender, street, zipcode, city, emailaddress, password, id, privileges, active) 
VALUES ('care', 'leer', 'Jan-10-1999', 'male', 'leer', '69120', 'leer', 'anbu', '202cb962ac59075b964b07152d234b70', '54331', '1', '1');

INSERT INTO users (name, forename, birthdate, gender, street, zipcode, city, emailaddress, password, id, privileges, active) 
VALUES ('patient', 'leer', 'Jan-10-1999', 'male', 'leer', '69120', 'leer', 'nin', '202cb962ac59075b964b07152d234b70', '0987654321', '0', '1');

INSERT INTO users (name, forename, birthdate, gender, street, zipcode, city, emailaddress, password, id, privileges, active) 
VALUES ('none','none','1212-12-12','male','leer','69120','leer','dummy','202cb962ac59075b964b07152d234b70','123456','0','0');

INSERT INTO registerConflicts (id, "timestamp") VALUES ('123456', '2002.07.10 AD at 15:08:56 PDT');

INSERT INTO unclearedconsents (id, filename, md5hash, participation, "timestamp") VALUES ('123456', 'asdasd', 'afasfgar', '1', '2002.07.10 AD at 15:08:56 PDT');