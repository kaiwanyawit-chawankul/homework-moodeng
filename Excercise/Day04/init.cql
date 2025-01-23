CREATE KEYSPACE IF NOT EXISTS your_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE your_keyspace;

CREATE TYPE IF NOT EXISTS mouse_data (
  x int,
  y int,
  time bigint
);

CREATE TABLE IF NOT EXISTS mouse_events (
  eventType text,
  eventTimestamp bigint,
  data frozen<list<frozen<mouse_data>>>,
  PRIMARY KEY (eventType, eventTimestamp)
);
