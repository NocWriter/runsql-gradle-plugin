-- Create new table.
CREATE TABLE books
(
    id     INT IDENTITY  PRIMARY KEY,
    name   VARCHAR(100),
    author VARCHAR(100)
);

-- Insert sample data to table.
INSERT INTO books (name, author)
VALUES ('The Hound of the Baskervilles', 'Arthur Conan Doyle');
