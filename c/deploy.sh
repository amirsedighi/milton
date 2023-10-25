#! /bin/sh
#
# This script performs the followings:
#	1.	k delete -n micro deployment spring-kafka-producer-app
#	2. 	eva $(minikube docker-env)
#	3.	docker image rm spring-kafka-producer-img
#	4.	mvn clean install -DskipTests
#	5.	eval $(minikube docker-env)
#	6.	docker build -t spring-kafka-consumer-img .
#	7.	k apply -f ./manifest

kubectl create namespace milton

eval $(minikube docker-env)
echo DELETE deployment
kubectl delete -n milton deployment stream-gamma-ms

sleep 2

eval $(minikube docker-env)

echo DELETE Image
docker image rm stream-milton-gamma-ms-img

sleep 1

echo COMPILE
mvn clean install -DskipTests


eval $(minikube docker-env)

echo BUILD Image
docker build -t milton-stream-gamma-ms-img .
 
sleep 1

echo DEPLOY Container
kubectl apply -f ./manifest


