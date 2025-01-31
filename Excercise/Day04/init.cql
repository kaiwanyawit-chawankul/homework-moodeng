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

CREATE TABLE IF NOT EXISTS mouse_events (
    -- event_id UUID PRIMARY KEY,     -- Unique ID for each event
    eventType TEXT,               -- Type of the mouse event (e.g., "click", "move")
    eventTimestamp TIMESTAMP,     -- Timestamp of the event
    data LIST<FROZEN<MouseData>>,   -- List of MouseData (using FROZEN for complex types)
    PRIMARY KEY (eventType, eventTimestamp)
);

CREATE TYPE IF NOT EXISTS MouseData (
    x INT,                         -- X-coordinate of the mouse
    y INT,                         -- Y-coordinate of the mouse
    time TIMESTAMP                    -- Timestamp of the mouse event in the data
);

CREATE TABLE IF NOT EXISTS mouse_events (
    id UUID PRIMARY KEY,     -- Unique ID for each event
    eventType TEXT,               -- Type of the mouse event (e.g., "click", "move")
    --eventTimestamp TIMESTAMP,     -- Timestamp of the event
    data BLOB,   -- List of MouseData (using FROZEN for complex types)
    --PRIMARY KEY (eventType, eventTimestamp)
);

CREATE TABLE IF NOT EXISTS heatmap (
    x INT,                         -- X-coordinate of the mouse
    y INT,                         -- Y-coordinate of the mouse
    count bigint,
    PRIMARY KEY (x, y)
);

