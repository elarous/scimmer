CREATE TABLE sub_items (
  id uuid default uuid_generate_v4(),
  type VARCHAR(50) NOT NULL,
  mapped_to VARCHAR(50) NOT NULL,
  collection VARCHAR(50) NOT NULL,
  array_attr_id uuid NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT fk_array_attr FOREIGN KEY (array_attr_id) REFERENCES array_attrs (id)
);

