#! /bin/sh


echo "|||||||||||| deploy all has been called"


echo "|||||||||||| deploying stream-alpha-ms"
cd a
./deploy.sh


echo "|||||||||||| deploying stream-beta-ms"
cd ../b
./deploy.sh


echo "|||||||||||| deploying stream-gamma-ms"
cd ../c
./deploy.sh

