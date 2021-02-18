CREATE TABLE single_attrs(
  id uuid default uuid_generate_v4(),
  name VARCHAR(50) NOT NULL,
  mapped_to VARCHAR(50) NOT NULL,
  collection VARCHAR(50) NOT NULL,
  container_id uuid NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_schema FOREIGN KEY (container_id) REFERENCES schemas (id),
  CONSTRAINT fk_extension FOREIGN KEY (container_id) REFERENCES extensions (id)
);
