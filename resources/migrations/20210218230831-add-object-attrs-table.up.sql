CREATE TABLE object_attrs (
  id uuid default uuid_generate_v4(),
  name VARCHAR(50) NOT NULL,
  schema_id uuid,
  extension_id uuid,

  PRIMARY KEY (id),
  CONSTRAINT fk_schema FOREIGN KEY (schema_id) REFERENCES schemas (id) ON DELETE CASCADE,
  CONSTRAINT fk_extension FOREIGN KEY (extension_id) REFERENCES extensions (id) ON DELETE CASCADE
);

