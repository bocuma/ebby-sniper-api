## Routes  [![Build Status](https://travis-ci.org/bocuma/ebby-sniper-api.svg?branch=master)](https://travis-ci.org/bocuma/ebby-sniper-api)

`POST /users`
Params `username` and `password`

`PUT /users/{username}`
Params `password`

`DELETE /users/{username}`

`POST /users/{username}/items`
Params `item-id` and `price`

`PUT /users/{username}/items/{item-id}`
Params `price`

`DELETE /users/{username}/items/{item-id}`


## Running

To start a web server for the application, run:

    lein ring server
