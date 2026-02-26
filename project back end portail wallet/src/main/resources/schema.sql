-- Agents table (all columns declared upfront)
CREATE TABLE IF NOT EXISTS agents (
    id INT NOT NULL AUTO_INCREMENT,
    agent_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    agent_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    otp_channel VARCHAR(10) DEFAULT 'EMAIL',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    id_type VARCHAR(50),
    id_number VARCHAR(100),
    commission DECIMAL(10,2) DEFAULT 0.00,
    rib VARCHAR(100),
    contract_type VARCHAR(100),
    contract_date DATE,
    signatory VARCHAR(255),
    patent_number VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    address_line3 VARCHAR(255),
    address_line4 VARCHAR(255),
    country VARCHAR(100),
    region VARCHAR(100),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Agent Privileges (many-to-many)
CREATE TABLE IF NOT EXISTS agent_privileges (
    id INT NOT NULL AUTO_INCREMENT,
    agent_id INT NOT NULL,
    privilege VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_agent_privilege (agent_id, privilege),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Agent Features (fonctionnalites accordees)
CREATE TABLE IF NOT EXISTS agent_features (
    id INT NOT NULL AUTO_INCREMENT,
    agent_id INT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_agent_feature (agent_id, feature),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id INT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    cin VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255),
    wallet_id VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'Actif',
    kyc_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id INT NOT NULL AUTO_INCREMENT,
    wallet_id VARCHAR(100) NOT NULL UNIQUE,
    owner_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    daily_limit DECIMAL(19,2),
    monthly_limit DECIMAL(19,2),
    currency VARCHAR(10) NOT NULL DEFAULT 'MAD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Transaction Logs
CREATE TABLE IF NOT EXISTS transaction_logs (
    id INT NOT NULL AUTO_INCREMENT,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    agent_id INT NOT NULL,
    client_name VARCHAR(255),
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    status VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    reference VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Transfers
CREATE TABLE IF NOT EXISTS transfers (
    id INT NOT NULL AUTO_INCREMENT,
    transfer_id VARCHAR(100) NOT NULL UNIQUE,
    agent_id INT NOT NULL,
    sender_wallet_id VARCHAR(100) NOT NULL,
    receiver_wallet_id VARCHAR(100) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    motif TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_wallet_id) REFERENCES wallets(wallet_id),
    FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(wallet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bill Payments
CREATE TABLE IF NOT EXISTS bill_payments (
    id INT NOT NULL AUTO_INCREMENT,
    payment_id VARCHAR(100) NOT NULL UNIQUE,
    agent_id INT NOT NULL,
    biller_id VARCHAR(100) NOT NULL,
    customer_ref VARCHAR(100) NOT NULL,
    contract_number VARCHAR(100),
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    status VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    invoice_ref VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id INT NOT NULL AUTO_INCREMENT,
    agent_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- OTP Codes
CREATE TABLE IF NOT EXISTS otp_codes (
    id INT NOT NULL AUTO_INCREMENT,
    agent_id INT NOT NULL,
    code VARCHAR(6) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Accounts
CREATE TABLE IF NOT EXISTS accounts (
    id INT NOT NULL AUTO_INCREMENT,
    rib VARCHAR(100) NOT NULL UNIQUE,
    account_id VARCHAR(100) NOT NULL UNIQUE,
    account_type VARCHAR(50) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
