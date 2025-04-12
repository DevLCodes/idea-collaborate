# Idea Collaborate

Simple Springboot application to add ideas, collaborate and vote on ideas.






- User Authentication using JWT tokens
- Storage in H2 in memory database
- Mockito unit tests





## APIs
### 1. Register the User

curl --location 'http://localhost:8080/register' \
--header 'Authorization;' \
--header 'Content-Type: application/json' \
--data '{
"userName": "testuser",
"password": "pass"
}'

### 2. Login

curl --location 'http://localhost:8080/login' \
--header 'Content-Type: application/json' \
--data '{
"userName" : "testuser",
"password" : "pass"
}'

Login will return an authorisation token and that has to be used in the header of all the APIs

### 3. Add Idea
curl --location 'http://localhost:8080/api/idea/add?userId=1' \
--header 'Authorization: Bearer <token>' \
--header 'Content-Type: application/json' \
--data '{
"title" : "Newton'\''s Idea 1",
"description" : "Gravity 1",
"tags":["Tag 1", "Tag 2"]

}'

### 4. Get Idea list
curl --location 'http://localhost:8080/api/idea/list?userId=1&sortBy=upvotes&direction=ASC' \
--header 'Authorization: Bearer  <token>' \

### 5. Vote an Id
curl --location --request POST 'http://localhost:8080/api/idea/1/vote?upvote=true&voterId=2' \
--header 'Authorization: Bearer <token>' \

### 6. Express Interest
curl --location --request POST 'http://localhost:8080/api/idea/1/expressInterest?upvote=true&userId=1' \
--header 'Authorization: Bearer <>' \
--header 'Cookie: JSESSIONID=AA53678D0964421408D83B9449C76B45'

### 7. Get interested collaborators
curl --location 'http://localhost:8080/api/idea/1/interestCollaborators?ideaId=1&userId=1' \
--header 'Authorization: Bearer <token' \
--header 'Cookie: JSESSIONID=AA53678D0964421408D83B9449C76B45'

## Run the app
./gradlew bootRun
