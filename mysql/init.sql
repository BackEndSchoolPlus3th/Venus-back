CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author VARCHAR(255),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modify_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- News 테이블 초기 데이터 삽입

INSERT INTO news (title, content, author, create_date, modify_date)
VALUES
    ('First News Title', 'This is the content of the first news article.', 'Author 1', NOW(), NOW()),
    ('Second News Title', 'This is the content of the second news article.', 'Author 2', NOW(), NOW()),
    ('Third News Title', 'This is the content of the third news article.', 'Author 3', NOW(), NOW());
