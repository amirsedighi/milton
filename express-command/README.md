
Scale Out:
kubectl scale --replicas=2 deployment express-command-ms -n milton

Reload All:
curl http://localhost:62144/control/reloadall

curl http://localhost:49813/control/reloadall    


Number of users holding at least one permission:
 select distinct count(user_id) from miltondb.user_permissions;


