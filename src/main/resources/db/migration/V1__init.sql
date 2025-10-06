CREATE TABLE IF NOT EXISTS tracks (
  track_id     SERIAL PRIMARY KEY,
  track_name   VARCHAR(255) NOT NULL,
  track_artist VARCHAR(255) NOT NULL,
  s3_key       VARCHAR(1024) NOT NULL UNIQUE
);