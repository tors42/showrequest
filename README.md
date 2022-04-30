# showrequest

HTTP server which logs (prints to standard output) and responds with a HTTP 200
and a response body containing the request (incoming request line, incoming
headers and incoming body).

# Build

Example building an image and tagging with name "showrequest"

    $ docker build --tag showrequest .
    ...
    Successfully tagged showrequest:latest

# Run

Example running a container detached ("in the background") named "showrequest"
from the image tagged "showrequest"

    $ docker run --rm --detach --name showrequest --env PORT=8000 --publish 8000:8000 showrequest
    f58b821d581fba598539c126d572a7dd88194d8a2b0c7d27e12d5f457fca1cd3

# Test local

The response body will contain the sent request

    $ curl --header "testing: hello" --header "Authorization: Bearer MySecret" http://127.0.0.1:8000/some/path
    GET /some/path HTTP/1.1
    Accept: */*
    Host: 127.0.0.1:8000
    User-agent: curl/7.83.0
    Authorization: Bearer ***
    Testing: hello

The request will be written to standard output of the container so it is
available by checking the container log

    $ docker logs showrequest
    GET /some/path HTTP/1.1
    Accept: */*
    Host: 127.0.0.1:8000
    User-agent: curl/7.83.0
    Authorization: Bearer ***
    Testing: hello

Stop container (which in this case will delete it, since it was started with
"--rm" option)

    $ docker stop --time 0 showrequest
    showrequest

# Test public

The app is deployed on Heroku

    $ curl --header "testing: hello" --header "Authorization: Bearer MySecret" https://showrequest.herokuapp.com/some/path
    GET /some/path HTTP/1.1
    Accept: */*
    Authorization: Bearer ***
    Connect-time: 0
    Connection: close
    Host: showrequest.herokuapp.com
    Testing: hello
    Total-route-time: 8330
    User-agent: curl/7.83.0
    Via: 1.1 vegur
    X-forwarded-for: ***
    X-forwarded-port: 443
    X-forwarded-proto: https
    X-request-id: f2c4f274-beea-43e6-9130-62f0cf1b3488
    X-request-start: 1651325641036
