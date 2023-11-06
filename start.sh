docker compose up -d --build --remove-orphans
sleep 10
docker compose exec jobmanager sql-gateway.sh start
