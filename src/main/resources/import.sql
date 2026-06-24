-- Roles iniciais
INSERT INTO tb_role (id, nome) VALUES ('1e000000-0000-0000-0000-000000000001', 'ADMIN');
INSERT INTO tb_role (id, nome) VALUES ('1e000000-0000-0000-0000-000000000002', 'USER');

-- Usuário Admin (Senha: admin123)
INSERT INTO tb_user (id, nome, email, senha, role_id) VALUES 
('3e000000-0000-0000-0000-000000000001', 'Administrador', 'admin@testshop.com', '$2a$10$rpjGOKl7ZWb5YyT9LOZbP.qR/meHWnJOTMDTOeAOQRLFN54sXSqtq', '1e000000-0000-0000-0000-000000000001');

-- Usuário Comum (Senha: user123)
INSERT INTO tb_user (id, nome, email, senha, role_id) VALUES
('3e000000-0000-0000-0000-000000000002', 'Cliente de Teste', 'user@testshop.com', '$2a$10$TjkjGLoIo528xkZXB42G0u5a/CtOJurpymP6/qsIvmRtBxs.aX8H2', '1e000000-0000-0000-0000-000000000002');

-- Produtos iniciais (Eletrônicos)
INSERT INTO tb_produto (id, nome, descricao, preco, quantidade, imagem, url) VALUES 
('2e000000-0000-0000-0000-000000000001', 'Smartphone Galaxy S24 Ultra', 'O mais potente da Samsung com IA integrada e S-Pen.', 7999.00, 10, 'https://images.unsplash.com/photo-1610945415295-d9baf0602165?auto=format&fit=crop&q=80&w=800', 'galaxy-s24-ultra'),
('2e000000-0000-0000-0000-000000000002', 'MacBook Air M3', 'O equilíbrio perfeito entre potência e portabilidade.', 9999.00, 5, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&q=80&w=800', 'macbook-air-m3'),
('2e000000-0000-0000-0000-000000000003', 'Sony WH-1000XM5', 'O melhor cancelamento de ruído do mercado.', 2499.00, 15, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&q=80&w=800', 'sony-wh-1000xm5'),
('2e000000-0000-0000-0000-000000000004', 'iPad Pro M4', 'O tablet mais avançado do mundo agora com tela OLED.', 12500.00, 8, 'https://images.unsplash.com/photo-1544244015-0cd4b3ffc6b0?auto=format&fit=crop&q=80&w=800', 'ipad-pro-m4');
