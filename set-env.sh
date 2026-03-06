#!/bin/bash

echo "Setting environment variables..."

export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=finance_db
export DB_USER=finance_user
export DB_PASSWORD=finance_pass

export APP_PORT=8080

echo "Environment variables configured."