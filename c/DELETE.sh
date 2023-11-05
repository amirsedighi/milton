#! /bin/sh


echo DELETE deployment
kubectl delete -n milton deployment stream-gamma-ms

echo DELETE Image
docker image rm stream-milton-gamma-ms-img

