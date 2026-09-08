INSERT INTO user_groups (name)
SELECT 'Admin'
WHERE NOT EXISTS (
    SELECT 1
    FROM user_groups
    WHERE name = 'Admin'
);

INSERT INTO user_groups (name)
SELECT 'User'
WHERE NOT EXISTS (
    SELECT 1
    FROM user_groups
    WHERE name = 'User'
);


INSERT INTO users (name, email, group_id)
SELECT
    'Dev Admin',
    'dev-admin@example.com',
    ug.id
FROM user_groups ug
WHERE ug.name = 'Admin'
  AND NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'dev-admin@example.com'
);


INSERT INTO suppliers (name)
SELECT 'Supplier A'
WHERE NOT EXISTS (
    SELECT 1
    FROM suppliers
    WHERE name = 'Supplier A'
);

INSERT INTO suppliers (name)
SELECT 'Supplier B'
WHERE NOT EXISTS (
    SELECT 1
    FROM suppliers
    WHERE name = 'Supplier B'
);


INSERT INTO storage_areas (name)
SELECT 'Warehouse A'
WHERE NOT EXISTS (
    SELECT 1
    FROM storage_areas
    WHERE name = 'Warehouse A'
);

INSERT INTO storage_areas (name)
SELECT 'Warehouse B'
WHERE NOT EXISTS (
    SELECT 1
    FROM storage_areas
    WHERE name = 'Warehouse B'
);


INSERT INTO storage_locations (name, storage_area_id)
SELECT
    'Shelf A1',
    sa.id
FROM storage_areas sa
WHERE sa.name = 'Warehouse A'
  AND NOT EXISTS (
    SELECT 1
    FROM storage_locations
    WHERE name = 'Shelf A1'
      AND storage_area_id = sa.id
);

INSERT INTO storage_locations (name, storage_area_id)
SELECT
    'Shelf B1',
    sa.id
FROM storage_areas sa
WHERE sa.name = 'Warehouse B'
  AND NOT EXISTS (
    SELECT 1
    FROM storage_locations
    WHERE name = 'Shelf B1'
      AND storage_area_id = sa.id
);