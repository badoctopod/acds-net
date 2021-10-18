---- TABLES ------------------------------------------------------------------
CREATE TABLE acds.net_devices (
  device_id          NUMBER GENERATED ALWAYS AS IDENTITY,
  device_ip          VARCHAR2(15 CHAR) NOT NULL,
  device_name        VARCHAR2(255 CHAR) NOT NULL,
  device_description VARCHAR2(255 CHAR),
  device_status      VARCHAR2(4 CHAR),
  coordinate_x       NUMBER(17,7),
  coordinate_y       NUMBER(17,7),
  CONSTRAINT net_devices_pk
    PRIMARY KEY (device_id)
)
/
