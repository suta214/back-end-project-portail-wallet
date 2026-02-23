-- Agents table
CREATE TABLE IF NOT EXISTS agents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_code TEXT NOT NULL UNIQUE,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    agent_type TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Agent Privileges (many-to-many)
CREATE TABLE IF NOT EXISTS agent_privileges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_id INTEGER NOT NULL,
    privilege TEXT NOT NULL,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE,
    UNIQUE(agent_id, privilege)
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    last_name TEXT NOT NULL,
    first_name TEXT NOT NULL,
    cin TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL UNIQUE,
    email TEXT,
    wallet_id TEXT,
    status TEXT NOT NULL DEFAULT 'Actif',
    kyc_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    wallet_id TEXT NOT NULL UNIQUE,
    owner_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    type TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIF',
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    daily_limit DECIMAL(19,2),
    monthly_limit DECIMAL(19,2),
    currency TEXT NOT NULL DEFAULT 'MAD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transaction Logs
CREATE TABLE IF NOT EXISTS transaction_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    transaction_id TEXT NOT NULL UNIQUE,
    type TEXT NOT NULL,
    agent_id INTEGER NOT NULL,
    client_name TEXT,
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    status TEXT NOT NULL DEFAULT 'EN_ATTENTE',
    reference TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- Transfers
CREATE TABLE IF NOT EXISTS transfers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    transfer_id TEXT NOT NULL UNIQUE,
    agent_id INTEGER NOT NULL,
    sender_wallet_id TEXT NOT NULL,
    receiver_wallet_id TEXT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    motif TEXT,
    status TEXT NOT NULL DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_wallet_id) REFERENCES wallets(wallet_id),
    FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(wallet_id)
);

-- Bill Payments
CREATE TABLE IF NOT EXISTS bill_payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    payment_id TEXT NOT NULL UNIQUE,
    agent_id INTEGER NOT NULL,
    biller_id TEXT NOT NULL,
    customer_ref TEXT NOT NULL,
    contract_number TEXT,
    amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2),
    status TEXT NOT NULL DEFAULT 'EN_ATTENTE',
    invoice_ref TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    entity TEXT NOT NULL,
    entity_id TEXT,
    details TEXT,
    ip_address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- Accounts
CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rib TEXT NOT NULL UNIQUE,
    account_id TEXT NOT NULL UNIQUE,
    account_type TEXT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_agents_email ON agents(email);
CREATE INDEX IF NOT EXISTS idx_agents_phone ON agents(phone);
CREATE INDEX IF NOT EXISTS idx_agents_agent_code ON agents(agent_code);
CREATE INDEX IF NOT EXISTS idx_clients_phone ON clients(phone);
CREATE INDEX IF NOT EXISTS idx_clients_cin ON clients(cin);
CREATE INDEX IF NOT EXISTS idx_wallets_wallet_id ON wallets(wallet_id);
CREATE INDEX IF NOT EXISTS idx_wallets_phone ON wallets(phone);
CREATE INDEX IF NOT EXISTS idx_transaction_logs_agent_id ON transaction_logs(agent_id);
CREATE INDEX IF NOT EXISTS idx_transaction_logs_transaction_id ON transaction_logs(transaction_id);
CREATE INDEX IF NOT EXISTS idx_transfers_agent_id ON transfers(agent_id);
CREATE INDEX IF NOT EXISTS idx_transfers_transfer_id ON transfers(transfer_id);
CREATE INDEX IF NOT EXISTS idx_bill_payments_agent_id ON bill_payments(agent_id);
CREATE INDEX IF NOT EXISTS idx_bill_payments_payment_id ON bill_payments(payment_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_agent_id ON audit_logs(agent_id);
