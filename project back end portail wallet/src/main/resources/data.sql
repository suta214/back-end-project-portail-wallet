-- Default Admin Agent
-- Password: admin123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'ADMIN001',
    'Admin User',
    'admin@portailagent.ma',
    '+212600000001',
    '$2a$10$F2rOsIwlDlOEBbIfCqDu8ueyxbWbCin.pUz5bgcZwUdb/Z9XqtfyG',
    'ADMIN',
    TRUE
);

-- Add privileges to admin
INSERT IGNORE INTO agent_privileges (agent_id, privilege) VALUES
    (1, 'CASH_IN'),
    (1, 'CASH_OUT'),
    (1, 'TRANSFER'),
    (1, 'BILL_PAYMENT'),
    (1, 'CLIENT_MGMT'),
    (1, 'WALLET_MGMT'),
    (1, 'HISTORY'),
    (1, 'PROFILE'),
    (1, 'AGENT_MGMT'),
    (1, 'REPORTS');

-- Test Agent 1
-- Password: agent123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'AGENT001',
    'Jean Dupont',
    'jean.dupont@example.ma',
    '+212600000002',
    '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi',
    'AGENT_PROPRE',
    TRUE
);

-- Add privileges to agent 1
INSERT IGNORE INTO agent_privileges (agent_id, privilege) VALUES
    (2, 'CASH_IN'),
    (2, 'CASH_OUT'),
    (2, 'TRANSFER'),
    (2, 'BILL_PAYMENT'),
    (2, 'HISTORY'),
    (2, 'PROFILE');

-- Back Office Agent
-- Password: agent123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'BO001',
    'Sara Alami',
    'sara.alami@example.ma',
    '+212600000003',
    '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi',
    'BACK_OFFICE',
    TRUE
);

-- Add privileges to BO001
INSERT IGNORE INTO agent_privileges (agent_id, privilege)
SELECT id, priv FROM agents
CROSS JOIN (SELECT 'CLIENT_MGMT' AS priv UNION SELECT 'WALLET_MGMT' UNION SELECT 'HISTORY' UNION SELECT 'AGENT_MGMT' UNION SELECT 'REPORTS' UNION SELECT 'PROFILE') p
WHERE agent_code = 'BO001';

-- Agent Mandaté Principal
-- Password: agent123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'MANDATE001',
    'Karim Bennani',
    'karim.bennani@example.ma',
    '+212600000004',
    '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi',
    'AGENT_MANDATE_PRINCIPAL',
    TRUE
);

-- Add privileges to MANDATE001
INSERT IGNORE INTO agent_privileges (agent_id, privilege)
SELECT id, priv FROM agents
CROSS JOIN (SELECT 'CASH_IN' AS priv UNION SELECT 'CASH_OUT' UNION SELECT 'TRANSFER' UNION SELECT 'CLIENT_MGMT' UNION SELECT 'WALLET_MGMT' UNION SELECT 'HISTORY' UNION SELECT 'PROFILE') p
WHERE agent_code = 'MANDATE001';

-- Agent Mandaté Commerçant
-- Password: agent123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'COMMERCANT001',
    'Fatima Zohra',
    'fatima.zohra@example.ma',
    '+212600000005',
    '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi',
    'AGENT_MANDATE_COMMERCANT',
    TRUE
);

-- Add privileges to COMMERCANT001
INSERT IGNORE INTO agent_privileges (agent_id, privilege)
SELECT id, priv FROM agents
CROSS JOIN (SELECT 'CASH_IN' AS priv UNION SELECT 'CASH_OUT' UNION SELECT 'HISTORY' UNION SELECT 'PROFILE') p
WHERE agent_code = 'COMMERCANT001';

-- Agent Mandaté Détaillant
-- Password: agent123
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'DETAILLANT001',
    'Youssef Hajji',
    'youssef.hajji@example.ma',
    '+212600000006',
    '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi',
    'AGENT_MANDATE_DETAILLANT',
    TRUE
);

-- Add privileges to DETAILLANT001
INSERT IGNORE INTO agent_privileges (agent_id, privilege)
SELECT id, priv FROM agents
CROSS JOIN (SELECT 'CASH_IN' AS priv UNION SELECT 'CASH_OUT' UNION SELECT 'HISTORY' UNION SELECT 'PROFILE') p
WHERE agent_code = 'DETAILLANT001';

-- Update password hashes to ensure they are correct (runs on every startup)
UPDATE agents SET password_hash = '$2a$10$F2rOsIwlDlOEBbIfCqDu8ueyxbWbCin.pUz5bgcZwUdb/Z9XqtfyG' WHERE agent_code = 'ADMIN001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'AGENT001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'BO001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'MANDATE001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'COMMERCANT001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'DETAILLANT001';

-- Super Admin ALL (Password: all123)
INSERT IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
VALUES (
    'ALL001',
    'Super Admin',
    'superadmin@portailagent.ma',
    '+212600000007',
    '$2a$10$F2rOsIwlDlOEBbIfCqDu8ueyxbWbCin.pUz5bgcZwUdb/Z9XqtfyG',
    'ALL',
    TRUE
);

UPDATE agents SET password_hash = '$2a$10$F2rOsIwlDlOEBbIfCqDu8ueyxbWbCin.pUz5bgcZwUdb/Z9XqtfyG' WHERE agent_code = 'ALL001';

-- Default Wallets
INSERT IGNORE INTO wallets (wallet_id, owner_name, phone, type, status, balance, daily_limit, currency)
VALUES
    ('ADMIN-WALLET-001', 'Admin Wallet', '+212600000001', 'Niveau 3', 'ACTIF', 100000.00, 1000000.00, 'MAD'),
    ('CLIENT-WALLET-001', 'Test Client 1', '+212600000010', 'Niveau 1', 'ACTIF', 5000.00, 50000.00, 'MAD'),
    ('CLIENT-WALLET-002', 'Test Client 2', '+212600000011', 'Niveau 1', 'ACTIF', 12000.00, 50000.00, 'MAD');

-- Default Clients
INSERT IGNORE INTO clients (last_name, first_name, cin, phone, email, wallet_id, status, kyc_verified)
VALUES
    ('Client', 'Test One', 'AA123456', '+212600000010', 'client1@example.ma', 'CLIENT-WALLET-001', 'Actif', TRUE),
    ('Client', 'Test Two', 'AA123457', '+212600000011', 'client2@example.ma', 'CLIENT-WALLET-002', 'Actif', TRUE);

-- Default Accounts
INSERT IGNORE INTO accounts (rib, account_id, account_type, balance)
VALUES
    ('0010000000000000000000001', 'ACC-0001', 'Personnel', 50000.00),
    ('0010000000000000000000002', 'ACC-0002', 'Marchand', 100000.00);
