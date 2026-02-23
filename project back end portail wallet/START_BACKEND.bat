@echo off
REM Script de démarrage du Backend Portail Agent
REM Configuration automatique et lancement

echo ========================================
echo  Backend Portail Agent - Démarrage
echo ========================================

REM Configurer JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.5
echo ✓ JAVA_HOME configuré: %JAVA_HOME%

REM Vérifier Java
echo ✓ Vérification de Java...
%JAVA_HOME%\bin\java -version

REM Aller au répertoire du projet
cd /d "%CD%"
echo ✓ Projet: %CD%

REM Nettoyer et compiler
echo.
echo ✓ Nettoyage et compilation...
call mvnw.cmd clean install -q

if %ERRORLEVEL% neq 0 (
    echo ✗ Erreur lors de la compilation!
    pause
    exit /b 1
)

REM Démarrer l'application
echo.
echo ✓ Démarrage de l'application...
echo ========================================
echo Accès: http://localhost:8080/api
echo Admin: ADMIN001 / admin123
echo ========================================
echo.

call mvnw.cmd spring-boot:run

pause
