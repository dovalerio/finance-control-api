@echo off

REM Define as variaveis para a sessao atual (set) e permanentemente (setx)

set DB_URL=jdbc:postgresql://localhost:5433/finance_db
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set APP_PORT=8080
set SPRING_PROFILES_ACTIVE=dev

setx DB_URL "jdbc:postgresql://localhost:5433/finance_db"
setx DB_USERNAME "postgres"
setx DB_PASSWORD "postgres"
setx APP_PORT "8080"
setx SPRING_PROFILES_ACTIVE "dev"

echo Variaveis configuradas para a sessao atual e permanentemente.
echo Execute: gradlew bootRun
pause