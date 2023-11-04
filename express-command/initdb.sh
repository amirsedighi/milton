curl -X POST -H "Content-Type: application/json" -d '{"title":"perm1", "description":"This is just a description", "publishdate":"2023-04-01"}' "http://localhost:49856/permissions"

curl -X POST -H "Content-Type: application/json" -d '{"username":"tomcat", "birthdate":"1973-04-01"}' "http://localhost:49856/users"

curl -X PUT "http://localhost:49856/changes?userId=1&permissionId=1"