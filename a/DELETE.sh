#! /bin/sh

echo DELETE deployment
kubectl delete -n milton deployment stream-alpha-ms

echo DELETE Image
docker image rm milton-stream-alpha-ms-img


