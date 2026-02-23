# 🚀 Guide Complet de Test du Backend Portail Agent

## 📋 Étape 1: Démarrer l'Application Backend

### Prérequis
- Java 17+ installé
- Maven installé
- Workspace du projet: `C:\Users\mbougnar\Downloads\back end project portail wallet\project back end portail wallet`

### Commandes

```bash
# 1. Aller au répertoire du projet
cd "C:\Users\mbougnar\Downloads\back end project portail wallet\project back end portail wallet"

# 2. Compiler et construire
mvn clean install

# 3. Lancer l'application
mvn spring-boot:run
```

**Résultat attendu:**
```
Tomcat started on port(s): 8080 (http)
Started PortailAgentApplication
```

La base de données SQLite `database.db` sera créée automatiquement au démarrage et les tables seront initialisées à partir de `schema.sql`.

---

## 🔑 Étape 2: Tester L'authentification (LOGIN)

### Test avec Postman

**1. POST /api/auth/login**

```
URL: http://localhost:8080/api/auth/login
Method: POST
Headers:
  Content-Type: application/json

Body (JSON):
{
  "agentId": "ADMIN001",
  "password": "admin123"
}
```

**Réponse attendue (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "agentId": "ADMIN001",
  "agentName": "Admin User",
  "agentCode": "ADMIN001",
  "agentType": "ADMIN",
  "privileges": [
    "CASH_IN",
    "CASH_OUT",
    "TRANSFER",
    "BILL_PAYMENT",
    "CLIENT_MGMT",
    "WALLET_MGMT",
    "HISTORY",
    "PROFILE",
    "AGENT_MGMT",
    "REPORTS"
  ]
}
```

---

## 👥 Étape 3: Tester les Endpoints Clients

### 3.1 Récupérer tous les clients
**GET /api/clients**

```
URL: http://localhost:8080/api/clients
Method: GET
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
```

**Réponse attendue:** Liste des clients

### 3.2 Créer un nouveau client
**POST /api/clients**

```
URL: http://localhost:8080/api/clients
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "firstName": "Ahmed",
  "lastName": "Khalil",
  "cin": "AA987654",
  "phone": "+212600000050",
  "email": "ahmed@example.com"
}
```

### 3.3 Mettre à jour un client
**PUT /api/clients/{id}**

```
URL: http://localhost:8080/api/clients/1
Method: PUT
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "firstName": "Ahmed",
  "lastName": "Khalil Updated",
  "cin": "AA987654",
  "phone": "+212600000050",
  "email": "ahmed.updated@example.com"
}
```

---

## 💼 Étape 4: Tester les Endpoints Portefeuille

### 4.1 Récupérer tous les portefeuilles
**GET /api/wallets**

```
URL: http://localhost:8080/api/wallets
Method: GET
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
```

### 4.2 Créer un portefeuille
**POST /api/wallets**

```
URL: http://localhost:8080/api/wallets
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "ownerName": "Ahmed Khalil",
  "phone": "+212600000050",
  "email": "ahmed@example.com",
  "type": "client",
  "dailyLimit": 10000,
  "monthlyLimit": 100000,
  "currency": "MAD"
}
```

### 4.3 Vérifier un portefeuille (pour transfert)
**GET /api/wallets/verify?phone=+212600000010**

```
URL: http://localhost:8080/api/wallets/verify?phone=%2B212600000010
Method: GET
Note: Pas besoin de token pour ce endpoint (public)
```

### 4.4 Appliquer une action sur un portefeuille
**POST /api/wallets/{id}/actions**

```
URL: http://localhost:8080/api/wallets/1/actions
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "action": "suspend",
  "reason": "Non-respect des conditions"
}
```

Actions disponibles: `suspend`, `reactivate`, `close`

---

## 💰 Étape 5: Tester les Transactions

### 5.1 Cash-In (Dépôt d'argent)
**POST /api/transactions/cash-in**

```
URL: http://localhost:8080/api/transactions/cash-in
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "accountId": "ACC-0001",
  "amount": 5000,
  "fees": 50,
  "clientPhone": "+212600000010"
}
```

### 5.2 Cash-Out (Retrait d'argent)
**POST /api/transactions/cash-out**

```
URL: http://localhost:8080/api/transactions/cash-out
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "accountId": "ACC-0001",
  "amount": 2000,
  "fees": 25,
  "clientPhone": "+212600000010"
}
```

### 5.3 Récupérer l'historique des transactions (Pagination)
**GET /api/transactions?page=1&pageSize=10**

```
URL: http://localhost:8080/api/transactions?page=1&pageSize=10
Method: GET
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
```

**Réponse attendue:**
```json
{
  "data": [...],
  "total": 100,
  "page": 1,
  "pageSize": 10,
  "totalPages": 10
}
```

---

## 🔄 Étape 6: Tester les Transferts

### POST /api/transfers
**Initier un transfert**

```
URL: http://localhost:8080/api/transfers
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "transferType": "P2P",
  "senderPhone": "+212600000010",
  "beneficiaryPhone": "+212600000011",
  "amount": 1000,
  "fees": 10,
  "reason": "Remboursement"
}
```

---

## 📄 Étape 7: Tester les Paiements de Factures

### POST /api/payments/bills
**Payer une facture**

```
URL: http://localhost:8080/api/payments/bills
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "billerId": "WATER_COMPANY",
  "customerRef": "CUSTOMER_123",
  "contractNumber": "CONTRACT_456",
  "amount": 500,
  "fees": 5
}
```

---

## 👤 Étape 8: Tester le Profil Agent

### 8.1 Récupérer le profil
**GET /api/profile**

```
URL: http://localhost:8080/api/profile
Method: GET
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
```

### 8.2 Mettre à jour le profil
**PUT /api/profile**

```
URL: http://localhost:8080/api/profile
Method: PUT
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "email": "admin.updated@portailagent.ma",
  "phone": "+212611111111",
  "fullName": "Admin User Updated",
  "language": "fr",
  "timezone": "Africa/Casablanca"
}
```

### 8.3 Changer le mot de passe
**PUT /api/profile/password**

```
URL: http://localhost:8080/api/profile/password
Method: PUT
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "currentPassword": "admin123",
  "newPassword": "NewPassword123!"
}
```

### 8.4 Mettre à jour les paramètres
**PUT /api/profile/settings**

```
URL: http://localhost:8080/api/profile/settings
Method: PUT
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "language": "en",
  "timezone": "Africa/Casablanca",
  "notificationsEnabled": true,
  "twoFactorEnabled": false
}
```

---

## 📊 Étape 9: Tester le Tableau de Bord

### GET /api/dashboard/stats
**Récupérer les statistiques du tableau de bord**

```
URL: http://localhost:8080/api/dashboard/stats
Method: GET
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
```

---

## 🔓 Étape 10: Tester la Gestion des Agents

### 10.1 Créer un nouvel agent
**POST /api/agents**

```
URL: http://localhost:8080/api/agents
Method: POST
Headers:
  Authorization: Bearer {TOKEN_FROM_LOGIN}
  Content-Type: application/json

Body:
{
  "identifiant": "AGENT002",
  "firstName": "Jean",
  "lastName": "Dupont",
  "agentType": "AGENT_PROPRE",
  "email": "jean.dupont@example.com",
  "phone": "+212600000003"
}
```

Le mot de passe par défaut sera: `DefaultPassword123`

---

## 🔄 Étape 11: Tester le Refresh Token

### POST /api/auth/refresh
**Renouveler le token JWT**

```
URL: http://localhost:8080/api/auth/refresh
Method: POST
Headers:
  Authorization: Bearer {TOKEN_EXISTANT}

Body: (Empty ou vide)
```

**Réponse attendue:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

## 📱 Étape 12: Intégrer avec le Frontend

### Configuration du Frontend Angular

1. **Dans `environment.ts` du projet Angular:**
```typescript
export const environment = {
  apiUrl: 'http://localhost:8080/api',
  production: false
};
```

2. **Dans le service HTTP Angular:**
```typescript
constructor(private http: HttpClient) {}

private getAuthHeader(): HttpHeaders {
  const token = localStorage.getItem('token');
  return new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
}
```

3. **Lancer le frontend:**
```bash
cd path-to-angular-project
ng serve --port 4200
```

4. **Backend et Frontend totalement connectés:** 🎉
   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:4200`
   - CORS configuré pour localhost:4200

---

## ✅ Checklist de Test

- [ ] Login fonctionne (ADMIN001/admin123)
- [ ] GET /clients retourne la liste
- [ ] POST /clients crée un client
- [ ] PUT /clients/{id} met à jour un client
- [ ] GET /wallets retourne les portefeuilles
- [ ] POST /wallets crée un portefeuille
- [ ] POST /transactions/cash-in fonctionne
- [ ] POST /transactions/cash-out fonctionne
- [ ] GET /transactions retourne avec pagination
- [ ] POST /transfers initie un transfert
- [ ] POST /payments/bills paye une facture
- [ ] GET /profile retourne le profil
- [ ] PUT /profile/password change le mot de passe
- [ ] PUT /profile/settings met à jour les paramètres
- [ ] GET /dashboard/stats retourne les stats
- [ ] POST /agents crée un agent
- [ ] POST /auth/refresh renouvelle le token

---

## 🐛 Dépannage

### Erreur: "database.db not found"
- Le fichier doit être créé automatiquement au démarrage
- Vérifiez les permissions d'écriture dans le dossier du projet

### Erreur: "Unauthorized 401"
- Vérifiez que le token JWT est correct
- Format: `Authorization: Bearer {TOKEN}`
- Token expire après 24 heures (86400000 ms)

### Erreur: "CORS error"
- Frontend sur `localhost:4200`?
- Backend sur `localhost:8080`?
- CORS configuré pour ces deux domaines ✅

### Erreur: "Invalid password"
- Utilisateur test: `ADMIN001`
- Mot de passe: `admin123`
- Ou créer un nouvel agent via endpoint

---

## 📝 Notes

- **Base de données:** SQLite `database.db` (auto-créée)
- **JWT Expiration:** 24 heures
- **Context Path:** `/api`
- **Port:** 8080
- **Authentification:** JWT Token
- **Validation:** Jakarta Bean Validation
- **CORS Origins:** `http://localhost:4200`, `http://localhost:3000`

Bonne chance! 🚀
