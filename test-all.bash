#!/usr/bin/env bash
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-all.bash
#
: ${HOST=localhost}
: ${PORT=8080}
: ${MOVIE_ID_NORMAL=1}
: ${MOVIE_ID_NOT_FOUND=13}
: ${MOVIE_ID_NO_COMM=113}
: ${MOVIE_ID_NO_RAT=213}
: ${MOVIE_ID_NO_SCR=313}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
    url=$@
    echo "Testing URL: $url"
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function testCompositeCreated() {

    # Expect that the Movie Composite for movieId $MOVIE_ID_NORMAL has been created with three ratings, three comments and three screenings
    if ! assertCurl 200 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NORMAL -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$MOVIE_ID_NORMAL" $(echo $RESPONSE | jq .movieId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".screenings | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local movieId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/movie-composite/${movieId} -s"
    curl -X POST http://$HOST:$PORT/movie-composite -H "Content-Type: application/json" --data "$composite"
    echo "Added movie for movie id:$movieId"
}

function setupTestData() {

    body="{\"movieId\":$MOVIE_ID_NO_COMM"
    body+=\
',"director": "director", "duration": 0, "genre": "genre", "releaseYear": 2020, "title":"movie 113",
"ratings":[
{"author": "author 1", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 1, "ratingNumber": 5},
{"author": "author 2", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 2, "ratingNumber": 5},
{"author": "author 3", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 3, "ratingNumber": 5}
], "screenings":[
{"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 1},
{"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 2},
{"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 3}
]}'
    recreateComposite 113 "$body"

    body="{\"movieId\":$MOVIE_ID_NO_RAT"
    body+=\
',"director": "director", "duration": 0, "genre": "genre", "releaseYear": 2020, "title":"movie 113",
    "comments":[
            {"author": "author 1", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 1, "commentText": "text 1"},
            {"author": "author 2", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 2, "commentText": "text 2"},
            {"author": "author 3", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 3, "commentText": "text 3"}
    ],
    "screenings":[
           {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 1},
           {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 2},
           {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 3}
    ]}'
    recreateComposite 213 "$body"

    body="{\"movieId\":$MOVIE_ID_NO_SCR"
    body+=\
',"director": "director", "duration": 0, "genre": "genre", "releaseYear": 2020, "title":"movie 113",
    "comments":[
          {"author": "author 1", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 1, "commentText": "text 1"},
          {"author": "author 2", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 2, "commentText": "text 2"},
          {"author": "author 3", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 3, "commentText": "text 3"}
    ],
    "ratings":[
             {"author": "author 1", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 1, "ratingNumber": 5},
             {"author": "author 2", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 2, "ratingNumber": 5},
             {"author": "author 3", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 3, "ratingNumber": 5}
    ]}'
    recreateComposite 313 "$body"

    body="{\"movieId\":$MOVIE_ID_NORMAL"
    body+=\
',"director": "director", "duration": 0, "genre": "genre", "releaseYear": 2020, "title":"movie 113",
    "comments":[
            {"author": "author 1", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 1, "commentText": "text 1"},
            {"author": "author 2", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 2, "commentText": "text 2"},
            {"author": "author 3", "commentDate": "2024-08-12T19:47:45.088Z", "commentId": 3, "commentText": "text 3"}
        ],
    "ratings":[
            {"author": "author 1", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 1, "ratingNumber": 5},
            {"author": "author 2", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 2, "ratingNumber": 5},
            {"author": "author 3", "ratingDate": "2024-08-12T19:47:45.088Z", "ratingId": 3, "ratingNumber": 5}
        ],
    "screenings":[
            {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 1},
            {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 2},
            {"cinemaName": "cinema", "location": "location", "price": 10, "screeningDate": "2024-08-12T19:47:45.088Z", "screeningId": 3}
    ]}'
    recreateComposite 1 "$body"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

#waitForService curl -X DELETE http://$HOST:$PORT/movie-composite/1
waitForService curl http://$HOST:$PORT/actuator/health

setupTestData

waitForMessageProcessing

# Verify that a normal request works, expect three ratings, three screenings and three comments
assertCurl 200 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NORMAL -s"
assertEqual "$MOVIE_ID_NORMAL" $(echo $RESPONSE | jq .movieId)
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 3 $(echo $RESPONSE | jq ".screenings | length")
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")

# Verify that a 404 (Not Found) error is returned for a non existing movieId (13)
assertCurl 404 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NOT_FOUND -s"

# Verify that no comments are returned for movieId 113
assertCurl 200 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NO_COMM -s"
assertEqual $MOVIE_ID_NO_COMM $(echo $RESPONSE | jq .movieId)
assertEqual 0 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 3 $(echo $RESPONSE | jq ".screenings | length")

# Verify that no ratings are returned for movieId 213
assertCurl 200 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NO_RAT -s"
assertEqual $MOVIE_ID_NO_RAT $(echo $RESPONSE | jq .movieId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 0 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 3 $(echo $RESPONSE | jq ".screenings | length")

# Verify that no screenings are returned for movieId 313
assertCurl 200 "curl http://$HOST:$PORT/movie-composite/$MOVIE_ID_NO_SCR -s"
assertEqual $MOVIE_ID_NO_SCR $(echo $RESPONSE | jq .movieId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 0 $(echo $RESPONSE | jq ".screenings | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a movieId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/movie-composite/-1 -s"
assertEqual "\"Invalid movieId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a movieId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/movie-composite/invalidMovieId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
fi

echo "End:" `date`