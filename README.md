[![Build Status](https://travis-ci.com/dat-nextlevel/dat3-semester-server.svg?branch=master)](https://travis-ci.com/dat-nextlevel/dat3-semester-server)

## Next Level backend start-code.
*This project is meant as start code for projects, exercises and exam tasks given at 3rd semester at http://cphbusiness.dk in the Study Program "AP degree in Computer Science"*

*Projects which are expected to use this start-code are projects that require all, or most of the following technologies:*
- *JPA and REST*
- *Testing, including database test*
- *Testing, including tests of REST-API's*
- *CI and CONTINUOUS DELIVERY*

### Preconditions
- In order to use this code, you should have a local developer setup + a "matching" droplet on Digital Ocean as described in the 3. semester guidelines
- Envoriment variables set on your deployed server should be set:
  - [PASSWORD_DEFAULT_USER, PASSWORD_DEFAULT_ADMIN, CONNECTION_STARTCODE]

### API Documentation
*The following endpoints are set in this startcode.*

[...] = headers required.

| Method    | URL                                    | Request Body (JSON)     | Response (JSON)                         | Error         |
|---        |---                                     |---                      |---                                      |---            |
| POST      | /api/login                             | Authentication (1.0)    | Authentication (1.1)                    | er(1)         |
| GET       | /api/ext                               |                         | External Fetch (2.0)                    |               |
| GET       | /api/info/user                         | [x-access-token]        | User Fetch  (3.0)                       | er(2)         |
| GET       | /api/ext/admin                         | [x-access-token]         | Admin Fetch (3.1)                       | er(2)         |

#### GET & POST Responses.
##### Authentication 1
1.0 Authentication BODY
```javascript
{
    "username": "john",
    "password": "password"
}
```
1.1. Authentication RESPONSE
```javascript
{
    "username": "john",
    "token": "612wata...."
}
```

##### External Fetch 2
2.0 External Fetch RESPONSE
```javascript
{
  "chuck": {
    "url": "https://api.chucknorris.io/jokes/pn_y-heotimuyfatmsvzng",
    "value": "When Bruce Banner gets mad, he turns into the Hulk. When the Hulk gets mad, he turns into Chuck Norris."
  },
  "dad": {
    "id": "IYT082EQukb",
    "joke": "Why was ten scared of seven? Because seven ate nine."
  },
  "anime": {
    "anime": "Inuyasha",
    "character": "Naraku",
    "quote": "I am not going to kill you - I am going to break you."
  },
  "tronald": {
    "value": "Hillary Clinton doesn't have the strength or the stamina to MAKE AMERICA GREAT AGAIN! #AmericaFirst\nhttps://t.co/G1MuLrjhW9",
    "href": "http://api.tronalddump.io/quote/H8yh7kz_QF2lGZNYRfy5aQ"
  },
  "jeopardy": {
    "question": "This gold medal U.S. swimmer won an ESPY Award as best female athlete for 1996",
    "answer": "Amy Van Dyken",
    "value": 500
  }
}
```

##### Authenticated routes
3.0 User Fetch
```javascript
{
    "msg": "Hello to User: xxx"
}
```

3.1 Admin Fetch
```javascript
{
    "msg": "Hello to Admin: xxx"
}
```

#### GET & POST Errors.
1.0 Invalid password
```javascript
{
    "code": 403,
    "message": "Invalid user name or password"
}
```

2.0 Not authenticated
```javascript
{
    "code": 403,
    "message": "Token not valid (timed out?)"
}
```

