CREATE TABLE schemas (
  resource VARCHAR ( 50 ) NOT NULL,
  name VARCHAR (50) NOT NULL,
  is_default BOOLEAN default false,
  created_at TIMESTAMP NOT NULL,
  contents JSONB
 );
