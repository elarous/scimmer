CREATE TABLE extensions (
  id uuid default uuid_generate_v4(),
  name VARCHAR(50) NOT NULL,
  schema_id uuid NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_schema FOREIGN KEY (schema_id) REFERENCES schemas(id)
);

