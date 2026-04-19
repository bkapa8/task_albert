CREATE DATABASE IF NOT EXISTS bibliot_uor_db;
USE bibliot_uor_db;


CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nomeCompleto VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'FUNCIONARIO', 'LEITOR') NOT NULL
);


CREATE TABLE IF NOT EXISTS livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    categoria INT NOT NULL,
    quantidade INT NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (categoria) REFERENCES categorias(id)
);


CREATE TABLE IF NOT EXISTS emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    livro_id INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao_real DATE,
    status ENUM('PENDENTE', 'ATIVO', 'CONCLUÍDO', 'RESERVADO', 'DEVOLVIDO') NOT NULL DEFAULT 'PENDENTE',
    
    prioridade INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (livro_id) REFERENCES livros(id)
);


INSERT INTO categorias (nome) VALUES
('Tecnologia'), 
('Literatura'), 
('Ciências Humanas'),
('Autoajuda e Negócios'), 
('Ciências Exatas'), 
('Arte e Design');


INSERT INTO usuarios (username, password, nomeCompleto, role) VALUES
('admin1', 'Admin123!', 'Albert Einstein', 'ADMIN'),
('admin2', 'Admin123!', 'Jacira Imaculada', 'ADMIN'),

('kelsen', 'Vegeta123!', 'Kelsen Matias', 'FUNCIONARIO'),
('kleberson', 'Vegeta123!', 'Kleberson Filomeno', 'FUNCIONARIO'),
('angelo', 'Vegeta123!', 'Angelo Narciso', 'FUNCIONARIO'),

('frank', 'Vegeta123!', 'Frankline Abrantes', 'LEITOR'),
('bruce', 'Vegeta123!', 'Bruce Wayne', 'LEITOR'),
('dick', 'Vegeta123!', 'Dick Grayson', 'LEITOR');

