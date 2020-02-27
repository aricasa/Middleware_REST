Idempotent

curl -u admin:admin -X POST http://localhost:4567/api/students -H 'Cache-Control: no-cache' -d '{"name":"Luca", "surname":"Verdi", "email":"luca.verdi@polimi.it"}'

{"status":201,"message":"Resource Created with id [673f3c45]"}

-- NOT! Idempotent 

curl -u admin:admin -X PUT http://localhost:4567/api/students/mrss1 -H 'Cache-Control: no-cache' -d '{"name":"Mario", "surname":"Rossi", "email":"mario.rossi@polimi.it"}'

-- first

{"status":201,"message":"Resource Created with id [mrss1]"}

-- second
{"status":409,"message":"mrss1 already exists"}


-- Idempotent

curl -u admin:admin -X GET http://localhost:4567/api/students/mrss1

curl -u admin:admin -X GET http://localhost:4567/api/students

-- NOT! Idempotent 

curl -u admin:admin -X DELETE http://localhost:4567/api/students/mrss1

{"status":200,"message":"Removed mrsrs1"}
