##gamma a Milton Project

Gamma is a microservice which 
1. receives messages through http requests
2. pushes the received messages into IMDG
3. Checkes if the number of messages exceeded to the maximum
4. pushes them into the milton-gamma-aggregated Kafka topic


Here are the steps:
To run the app on your local machine:
$> mvn sprint-boot:run
OR
Run it inside IDE

Check the endpoint:
$> curl localhost:8080/hello
Hello!


To try it using minikube:
- Start Docker Desktop
- $> mvn clean install
  $> minikube -p minikube docker-env          # Optional
- $> eval $(minikube -p minikube docker-env)  # Optional
- $> minikube start
- $> minikube dashboard
- $> k cluster-info                           # Optional
- $> eval $(minikube docker-env)  # right before building the image
- $> docker build -t milton-gamma-ms-img .
- $> k apply -f ./manifest
- $> k get services -n milton
- $> minikube service gamma-ms-svc -n milton
- $> curl curl http://SERVICE:PORT/app/hello

