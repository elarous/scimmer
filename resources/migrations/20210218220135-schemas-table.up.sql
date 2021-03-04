CREATE TABLE schemas (
  id uuid default uuid_generate_v4(),
  resource VARCHAR ( 50 ) NOT NULL,
  name VARCHAR (50) NOT NULL,
  is_default BOOLEAN default false,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
 );
