#! /bin/sh


echo DELETE deployment
kubectl delete -n milton deployment stream-beta-ms


echo DELETE Image
docker image rm milton-stream-beta-ms-img
