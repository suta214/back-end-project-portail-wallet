-- Default Admin Agent
-- Password: admin123
INSERT OR IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
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
INSERT OR IGNORE INTO agent_privileges (agent_id, privilege)
SELECT 1, 'CASH_IN' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'CASH_OUT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'TRANSFER' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'BILL_PAYMENT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'CLIENT_MGMT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'WALLET_MGMT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'HISTORY' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'PROFILE' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'AGENT_MGMT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1)
UNION ALL
SELECT 1, 'REPORTS' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 1);

-- Test Agent 1
-- Password: agent123
INSERT OR IGNORE INTO agents (agent_code, full_name, email, phone, password_hash, agent_type, enabled)
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
INSERT OR IGNORE INTO agent_privileges (agent_id, privilege)
SELECT 2, 'CASH_IN' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2)
UNION ALL
SELECT 2, 'CASH_OUT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2)
UNION ALL
SELECT 2, 'TRANSFER' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2)
UNION ALL
SELECT 2, 'BILL_PAYMENT' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2)
UNION ALL
SELECT 2, 'HISTORY' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2)
UNION ALL
SELECT 2, 'PROFILE' WHERE EXISTS(SELECT 1 FROM agents WHERE id = 2);

-- Update password hashes to ensure they are correct (runs on every startup)
UPDATE agents SET password_hash = '$2a$10$F2rOsIwlDlOEBbIfCqDu8ueyxbWbCin.pUz5bgcZwUdb/Z9XqtfyG' WHERE agent_code = 'ADMIN001';
UPDATE agents SET password_hash = '$2a$10$Y.wzsh0.NNhkO39AtOGIH.dcZmG.RC7RNyrvjXVSb.zndMYDxOWTi' WHERE agent_code = 'AGENT001';

-- Default Wallets
INSERT OR IGNORE INTO wallets (wallet_id, owner_name, phone, type, status, balance, daily_limit, currency)
VALUES 
    ('ADMIN-WALLET-001', 'Admin Wallet', '+212600000001', 'Agent', 'ACTIF', 100000.00, 1000000.00, 'MAD'),
    ('CLIENT-WALLET-001', 'Test Client 1', '+212600000010', 'Personnel', 'ACTIF', 5000.00, 50000.00, 'MAD'),
    ('CLIENT-WALLET-002', 'Test Client 2', '+212600000011', 'Personnel', 'ACTIF', 12000.00, 50000.00, 'MAD');

-- Default Clients
INSERT OR IGNORE INTO clients (last_name, first_name, cin, phone, email, wallet_id, status, kyc_verified)
VALUES 
    ('Client', 'Test One', 'AA123456', '+212600000010', 'client1@example.ma', 'CLIENT-WALLET-001', 'Actif', TRUE),
    ('Client', 'Test Two', 'AA123457', '+212600000011', 'client2@example.ma', 'CLIENT-WALLET-002', 'Actif', TRUE);

-- Default Accounts
INSERT OR IGNORE INTO accounts (rib, account_id, account_type, balance)
VALUES 
    ('0010000000000000000000001', 'ACC-0001', 'Personnel', 50000.00),
    ('0010000000000000000000002', 'ACC-0002', 'Marchand', 100000.00);
