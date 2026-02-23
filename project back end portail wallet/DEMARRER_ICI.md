# 🚀 DÉMARRAGE RAPIDE

## Étape 1: Redémarrer VS Code
1. **Ferme VS Code complètement**
2. **Ouvre le dossier du projet:** `C:\Users\mbougnar\Downloads\back end project portail wallet\project back end portail wallet`
3. **Attends 15 secondes** pour que VS Code réindexe les fichiers

---

## Étape 2: Démarrer le Backend

### Option A: Avec le script batch (Recommandé) ✅
**Double-clique sur:** `START_BACKEND.bat`

Le script va automatiquement:
- ✓ Configurer JAVA_HOME
- ✓ Nettoyer le projet
- ✓ Compiler tout
- ✓ Démarrer le serveur

### Option B: Avec PowerShell
```powershell
# Ouvre PowerShell dans le dossier du projet
.\START_BACKEND.ps1
```

### Option C: Manuellement
```bash
# Ouvre PowerShell au dossier du projet
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.5"
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

---

## Étape 3: Vérifier que c'est OK

**Quand tu vois ce message:**
```
Tomcat started on port(s): 8080 (http)
Started PortailAgentApplication
```

C'est **PRÊT!** ✅

---

## Étape 4: Tester avec Postman

**Login (copier-coller exactement):**

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "agentId": "ADMIN001",
  "password": "admin123"
}
```

**Réponse attendue:** Tu reçois un JWT token ✅

---

## 📋 Quick Reference

| Ressource | URL |
|-----------|-----|
| Backend | http://localhost:8080 |
| API | http://localhost:8080/api |
| Login | POST /auth/login |
| Clients | GET /clients |
| Portefeuilles | GET /wallets |
| Transactions | GET /transactions |

**Credentials:**
- Admin: `ADMIN001` / `admin123`
- Agent Test: `AGENT001` / `agent123`

---

## 🆘 Dépannage

**Problem:** "JAVA_HOME not found"
- **Solution:** Regarde que C:\Program Files\Java\jdk-17.0.5 existe

**Problem:** Port 8080 déjà utilisé
- **Solution:** Tue le processus Java: `taskkill /IM java.exe /F`

**Problem:** Erreurs de compilation
- **Solution:** Supprime le dossier `target/` et réessaie

---

**C'est tout!** Juste double-clique sur `START_BACKEND.bat` et c'est lancé! 🎉
