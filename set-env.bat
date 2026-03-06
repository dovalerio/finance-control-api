@echo off

setx DB_HOST localhost
setx DB_PORT 5432
setx DB_NAME finance_db
setx DB_USER finance_user
setx DB_PASSWORD finance_pass

setx APP_PORT 8080

echo Environment variables configured.
pause