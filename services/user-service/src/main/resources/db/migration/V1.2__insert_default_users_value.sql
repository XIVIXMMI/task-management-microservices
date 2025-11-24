-- ADD 'CREATE' Action in constraints
ALTER TABLE role_permissions
DROP CONSTRAINT IF EXISTS role_permissions_action_check;

ALTER TABLE role_permissions
ADD CONSTRAINT role_permissions_action_check
CHECK (action IN ('CREATE','READ', 'UPDATE', 'DELETE', 'MANAGE'));

-- ADMIN DEFAULT USER INSERTION SCRIPT
INSERT INTO users (id, email, password_hash, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin.00@email.com',
    '$2a$10$DDWxKvYjlFoGZNWEGG7tgu8VQmpyHMNNac.YO3N7d2R1ZM4sHHmqq', -- BCrypt hash of "Abc@1234"
    NOW(),
    NOW()
    );

INSERT INTO profiles (id, user_id, first_name, last_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    'Adam',
    'Smith',
    NOW(),
    NOW()
    );

INSERT INTO roles (id, name, description, created_by, updated_by, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'ADMIN',
    'Administrator role with full permissions',
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    NOW(),
    NOW()
);

INSERT INTO role_permissions (role_id, resource, action)
VALUES
    ((SELECT id FROM roles WHERE name = 'ADMIN'),'TASK','MANAGE'),
    ((SELECT id FROM roles WHERE name = 'ADMIN'),'FEATURE','MANAGE'),
    ((SELECT id FROM roles WHERE name = 'ADMIN'),'OBJECTIVE','MANAGE');
-- MANAGE is the highest level of permission

INSERT INTO user_roles (user_id, role_id, assigned_at)
VALUES (
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    NOW()
    );

-- NORMAL USER INSERTION SCRIPT
INSERT INTO users (id, email, password_hash, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'user.00@email.com',
    '$2a$10$xv3CUBta93Z0IPc5p6PGy.CGOYDnatl9hfResI3oRj5fCej3BSA8S',
    NOW(),
    NOW()
    );

INSERT INTO profiles (id, user_id, first_name, last_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'user.00@email.com'),
    'Jane',
    'Doe',
    NOW(),
    NOW()
    );

INSERT INTO roles (id, name, description, created_by, updated_by, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'USER',
    'Standard user role with limited permissions',
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    (SELECT id FROM users WHERE email = 'admin.00@email.com'),
    NOW(),
    NOW()
    );

INSERT INTO role_permissions (role_id, resource, action)
VALUES
    ((SELECT id FROM roles WHERE name = 'USER'),'OBJECTIVE','READ'),
    ((SELECT id FROM roles WHERE name = 'USER'),'FEATURE','READ'),
    ((SELECT id FROM roles WHERE name = 'USER'),'FEATURE','UPDATE'),
    ((SELECT id FROM roles WHERE name = 'USER'),'TASK','CREATE'),
    ((SELECT id FROM roles WHERE name = 'USER'),'TASK','READ'),
    ((SELECT id FROM roles WHERE name = 'USER'),'TASK','UPDATE'),
    ((SELECT id FROM roles WHERE name = 'USER'),'TASK','DELETE');
    -- USER role has READ, UPDATE permissions on TASK and FEATURE and DELETE on TASK but only READ on OBJECT


INSERT INTO user_roles (user_id, role_id, assigned_at)
VALUES (
    (SELECT id FROM users WHERE email = 'user.00@email.com'),
    (SELECT id FROM roles WHERE name = 'USER'),
    NOW()
);

