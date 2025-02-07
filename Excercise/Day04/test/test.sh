#!/bin/bash
curl -f http://nginx || exit 1
curl -f http://scala-backend2:8081/heatmap || exit 1
curl -f http://scala-backend:8080/healthcheck || exit 1