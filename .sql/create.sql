CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    subtitle VARCHAR(100),
    body TEXT,
    publish_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_accepted BOOLEAN,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE images(
    id BIGINT auto_increment PRIMARY KEY,
    path VARCHAR(255) not null,
    article_id BIGINT,
    FOREIGN KEY (article_id) REFERENCES articles(id)
);

CREATE TABLE career_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    body TEXT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    is_checked BOOLEAN
);

CREATE TABLE users_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    role_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
