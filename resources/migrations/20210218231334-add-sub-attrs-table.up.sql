CREATE TABLE sub_attrs (
  id uuid default uuid_generate_v4(),
  name VARCHAR(50) NOT NULL,
  mapped_to VARCHAR(50) NOT NULL,
  collection VARCHAR(50) NOT NULL,
  object_attr_id uuid NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT fk_object_attr FOREIGN KEY (object_attr_id) REFERENCES object_attrs (id) ON DELETE CASCADE
);

