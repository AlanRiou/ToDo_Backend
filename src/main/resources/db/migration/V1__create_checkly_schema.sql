CREATE TABLE users (
  id BINARY(16) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL,
  firebase_uuid VARCHAR(128),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  role VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT uk_users_email UNIQUE (email),
  CONSTRAINT uk_users_firebase_uuid UNIQUE (firebase_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE task_lists (
  id BINARY(16) NOT NULL,
  user_id BINARY(16) NOT NULL,
  title VARCHAR(150) NOT NULL,
  description VARCHAR(500),
  accent_color VARCHAR(32),
  icon VARCHAR(64),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_task_lists_user_id (user_id),
  CONSTRAINT fk_task_lists_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE todos (
  id BINARY(16) NOT NULL,
  user_id BINARY(16) NOT NULL,
  task_list_id BINARY(16),
  title VARCHAR(150) NOT NULL,
  description VARCHAR(500),
  completed BOOLEAN NOT NULL,
  priority VARCHAR(20),
  due_date DATETIME(6),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_todos_user_id (user_id),
  INDEX idx_todos_task_list_id (task_list_id),
  INDEX idx_todos_due_date (due_date),
  CONSTRAINT fk_todos_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE,
  CONSTRAINT fk_todos_task_list
    FOREIGN KEY (task_list_id) REFERENCES task_lists (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
