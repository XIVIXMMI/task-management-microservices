-- users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Indexes for users
CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_deleted_at ON users(deleted_at);

-- profiles table
CREATE TABLE profiles (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL,  -- Links to users(id)
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    photo_url VARCHAR(500),
    date_of_birth DATE,
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),
    bio TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    -- Foreign key with CASCADE
    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE  -- delete user → delete profile
);

-- roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Indexes for roles
CREATE INDEX idx_roles_name ON roles(name) WHERE deleted_at IS NULL;
CREATE INDEX idx_roles_deleted_at ON roles(deleted_at);

-- user_roles join table (Many-to-Many)
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- ← Add audit field ( extra field, JPA don't use this)

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,  -- delete user → delete assignments

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE   -- delete role → delete assignments
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- role_permissions table (ElementCollection)
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    resource VARCHAR(50) NOT NULL CHECK (resource IN ('TASK', 'FEATURE', 'OBJECTIVE')),
    action VARCHAR(50) NOT NULL CHECK (action IN ('READ', 'WRITE', 'DELETE', 'MANAGE')),

    PRIMARY KEY (role_id, resource, action),
    CONSTRAINT fk_role_permissions_role
            FOREIGN KEY (role_id)
            REFERENCES roles(id)
            ON DELETE CASCADE  -- delete role → delete permissions
);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);

COMMENT ON TABLE users IS 'Core user authentication and basic info';
COMMENT ON TABLE profiles IS 'Extended user profile information';
COMMENT ON TABLE roles IS 'System roles for permission management';
COMMENT ON TABLE user_roles IS 'Many-to-many relationship between users and roles';
COMMENT ON TABLE role_permissions IS 'Permissions assigned to each role';

COMMENT ON COLUMN users.deleted_at IS 'Soft delete timestamp - NULL means active';
COMMENT ON COLUMN profiles.gender IS 'User gender - constrained to specific values';
COMMENT ON COLUMN role_permissions.resource IS 'Resource type that permission applies to';
COMMENT ON COLUMN role_permissions.action IS 'Action allowed on the resource';
