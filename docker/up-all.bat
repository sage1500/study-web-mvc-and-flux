@echo off
set base=%~d0%~p0

docker-compose -f %base%db\docker-compose.yml up -d
docker-compose -f %base%keycloak\docker-compose.yml up -d
