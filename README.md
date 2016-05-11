# Spatial Transcriptomics Research API

A RESTFul API to provide access to the Spatial Transcriptomics Research data stored in a  MongoDB database.

This is the source code of our RESTful API from
which we can access the data stored in our database. 
Authorization trough OAuth2 is required.

A full manual on how to build and deploly will be added soon.

A full manual and descriptions of the endpoints will be added soon. 

A full manual on how to create and deploy the MongoDB database will be added soon.

The ST Viewer (link here) can be built and configured to work with this API
to access and visualize the data. 

See LICENSE file for licensing and references. 

#### Authorization Documentation

We use OAuth2 for authorization. See http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified for an introduction to OAuth2 and the different authorization flows. 

OAuth2 has the notion of authorization servers (handling authorization) and resource servers (delivering the content). We run authorization server and resource server in the same web application (called API). 

The API supports the OAuth2 password flow. This flow is used by all our applications at the moment. It means that the credentials to authenticate/authorize to the API are hard-coded in the applications. 

## Authorization Request

The applications send the following requests the authorization server to authenticate and authorize at the API:

Option 1: Retrieve an access token by login credentials:

POST https://your-server.com/api/oauth/token
grant_type=password
client_id=st-viewer-client
client_secret=<client secret>
username=<username>
password=<password>

A manual call with parameters passed as a query string (avoid, passwords may be logged) might look like:

https://your-server.com/api/oauth/token?grant_type=password&client_id=st-viewer-client&client_secret=<secret>&username=<username>&password=<password>

A typical response is shown further down in the document.

Option 2: Retrieve an access token by the refreshing with an already acquired refresh token from option 1:

POST https://your-server.com/api/oauth/token
grant_type=refresh_token
client_id=st-viewer-client
client_secret=<client secret>
refresh_token=<refresh token>

The response is similar to option 1.
A manual call with parameters passed as a query string (avoid, passwords and tokens may be logged) might look like:

https://your-server.com/api/oauth/token?grant_type=refresh_token&client_id=st-viewer-client&client_secret=<client secret>&refresh_token=<refresh token>

Authorization Response

The authorization server responds with a HTTP 200 and JSON payload containing the access token. It responds with a HTTP 400 and a JSON error message if the authorization fails. Note the difference between the access token (used for making rest calls to the API services as described below) and the refresh token (used for retrieving a new access token, as described above).

Server Response OK

HTTP/1.1 200 OK
     Content-Type: application/json;charset=UTF-8
     Cache-Control: no-store
     Pragma: no-cache
     {
       "access_token":"2YotnFZFEjr1zCsicMWpAA",
       "token_type":"example",
       "expires_in":3600,
       "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
       "example_parameter":"example_value"
     }



Server Response ERROR

HTTP/1.1 400 Bad Request
     Content-Type: application/json;charset=UTF-8
     Cache-Control: no-store
     Pragma: no-cache

     {
       "error":"invalid_request"
     }


API Requests

After successful authorization, the applications send the access token in the header of each HTTP request to the API. For testing purposes you can also send the access token as a query string parameter (avoid, may be logged), e.g.:

https://your-server.com/api/rest/account?access_token=<access token>
