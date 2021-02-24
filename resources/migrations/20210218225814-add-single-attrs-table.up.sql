CREATE TABLE single_attrs(
  id uuid default uuid_generate_v4(),
  name VARCHAR(50) NOT NULL,
  mapped_to VARCHAR(50) NOT NULL,
  collection VARCHAR(50) NOT NULL,
  schema_id uuid,
  extension_id uuid,

  PRIMARY KEY (id),
  CONSTRAINT fk_schema FOREIGN KEY (schema_id) REFERENCES schemas (id),
  CONSTRAINT fk_extension FOREIGN KEY (extension_id) REFERENCES extensions (id)
);
