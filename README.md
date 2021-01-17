# Role-Based-Action-Control-System
Assignment for Locus.sh

This is a basic Springboot project. App will start with some base data, and does not have any CRUD API availibility.


# Compile the project
mvn clean install
mvn clean test

# Run the project
java -jar target/role_based_access_control.jar

# API contract to check validity of a user over a resource
curl call: 
curl -X GET \
  'http://localhost:8080/v1/rbac/validate?user_id=<user_id>&resource_id=<resource_id>&action=<action>' \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 1febf0df-84cb-e8bc-3aa8-865c76c089b2'
  
Sample curl call: 
curl -X GET \
  'http://localhost:8080/v1/rbac/validate?user_id=1&resource_id=7b5fcb89-128f-4199-81b5-80fdc1ced73c&action=write' \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 1febf0df-84cb-e8bc-3aa8-865c76c089b2'
 
Sample Success Response:
{
    "status_code": "SUCCESS",
    "status_message": "All is good.",
    "data": {
        "user_allowed": <T/F>
    }
}
