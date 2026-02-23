#!/usr/bin/env pwsh
# Script de démarrage du Backend Portail Agent
# Exécution: ./START_BACKEND.ps1

Write-Host "========================================"
Write-Host "  Backend Portail Agent - Démarrage"
Write-Host "========================================"
Write-Host ""

# Configurer JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.5"
Write-Host "✓ JAVA_HOME configuré: $env:JAVA_HOME" -ForegroundColor Green

# Vérifier Java
Write-Host "✓ Vérification de Java..."
& "$env:JAVA_HOME\bin\java" -version 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Java n'a pas pu être trouvé!" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour quitter"
    exit 1
}

# Afficher le répertoire
$projectPath = Get-Location
Write-Host "✓ Projet: $projectPath" -ForegroundColor Green
Write-Host ""

# Nettoyer et compiler
Write-Host "✓ Nettoyage et compilation..." -ForegroundColor Yellow
Write-Host ""
& ".\mvnw.cmd" clean install

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erreur lors de la compilation!" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour quitter"
    exit 1
}

# Démarrer l'application
Write-Host ""
Write-Host "✓ Démarrage de l'application..." -ForegroundColor Green
Write-Host "========================================"
Write-Host "Accès: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "Admin: ADMIN001 / admin123" -ForegroundColor Cyan
Write-Host "========================================"
Write-Host ""

& ".\mvnw.cmd" spring-boot:run

Read-Host "Appuyez sur Entrée pour quitter"
