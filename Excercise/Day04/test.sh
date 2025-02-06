curl -f http://localhost || exit 1
curl -f http://localhost:8081/heatmap || exit 1
curl -f http://localhost:8080/healthcheck || exit 1