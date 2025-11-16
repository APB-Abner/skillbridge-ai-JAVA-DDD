
INSERT INTO users (nome, email, cpf) VALUES
                                         ('Abner', 'abner@fiap.com', '12345678901'),
                                         ('Clara', 'clara@fiap.com', '22233344455');

INSERT INTO trilhas (titulo, descricao, ativa) VALUES
                                                   ('Java & DDD', 'Fundamentos de arquitetura e domínio', TRUE),
                                                   ('ML Básico', 'Intro a modelos e treino', TRUE);

-- exemplo de matrícula
INSERT INTO matriculas (user_id, trilha_id, criada_em)
VALUES (1, 1, CURRENT_TIMESTAMP);
