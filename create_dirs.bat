@echo off
REM Create directory structure for Account Service

cd /d "c:\Bank Project\accountservice\src\main\java"

mkdir com\bank\accountservice\entity
mkdir com\bank\accountservice\repository
mkdir com\bank\accountservice\service
mkdir com\bank\accountservice\controller

echo Directory structure created successfully!
pause
