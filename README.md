# Cab Management Portal
Assignment for PhonePe

# Compile the project
mvn clean install
mvn clean test

# Run the project
java -jar target/cab-management-portal.jar

# APIs:
**1. Welcome API:**
curl call:
curl --location --request GET 'http://localhost:8080/'

**2. Health Check API**
curl call:
curl --location --request GET 'http://localhost:8080/ping'

**3. Register a Cab**
curl call:
curl --location --request POST 'http://localhost:8080/v1/register/cab' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cab_id": "RJ-13 CC 22093",
    "cab_state": "IDLE",
    "city_id": "bangalore1"
}'

**4. Onboard a city**
curl call:
curl --location --request POST 'http://localhost:8080/v1/register/city' \
--header 'Content-Type: application/json' \
--data-raw '{
    "city": "bangalore1"
}'

**5. Update Location of a Cab**
curl call:
curl --location --request POST 'http://localhost:8080/v1/location/update' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cab_id": "RJ-13 CC 22093",
    "location" : "BANGALORE2"
}'

**6. Update state of a cab**
curl call:
curl --location --request POST 'http://localhost:8080/v1/state/update' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cab_id": "RJ-13 CC 22093",
    "state" : "ON_TRIP"
}'

**7. Book a cab**
curl call:
curl --location --request POST 'http://localhost:8080/v1/trips/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "city": "bangalore1"
}'

**8. Get total idle time of a cab**
curl call:
curl --location --request GET 'http://localhost:8080/v1/analytics/getIdleTime' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cab_id": "RJ-13 CC 22093",
    "start_time":1610869361000, 
    "end_time": 1610959361000
}'

**9. Get all states of a cab**
curl call:
curl --location --request GET 'http://localhost:8080/v1/analytics/getCabStates' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cab_id": "RJ-13 CC 22093",
    "start_time":1610869361000, 
    "end_time": 1610959361000
}'

