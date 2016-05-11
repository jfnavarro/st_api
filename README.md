# Spatial Transcriptomics Research API

A RESTFul API to provide access to the Spatial Transcriptomics Research data stored in a  MongoDB database.

This is the source code of our RESTful API from
which we can access the data stored in our database. 
Authorization trough OAuth2 is required.

A full manual and descriptions of the endpoints will be added soon
but you can now see the documents api_endpoints.doc and datamodel.doc
for a basic description.

A full manual on how to create and deploy the MongoDB database will be added soon.

The ST Viewer (link here) can be built and configured to work with this API
to access and visualize the data, you just need to make sure that the
in the ST Viewer configuration the URL and clientID are the same as the API. 

See LICENSE file for licensing and references. 

## General Documentation

#### Backend System Overview

We run a Java web application  in a Tomcat servlet container. We use Java 1.6 and Tomcat 7.0.x

The main frameworks used are Spring 3.2.x, spring-security, spring-security-oauth, spring-data-mongodb, AWS SDK.

###### OAuth2
We use OAuth2 to authenticate at the API. The API application implements an OAuth2 server. The Admin application implements an OAuth2 client. The application uses the OAuth2 password flow to authenticate at the API/OAuth server. The Admin tool uses OAuth2 and a normal Spring Security auth mechanism. It authenticates at the API be sending the credentials that the user has entered through a OAuth2 password flow. If it successfully authenticated the API, it gets the current Account details from the API and checks the “role”. If the role is “ROLE_CM” or “ROLE_ADMIN” it authorizes the user to access the Admin application. ROLE_CM does not have access to the accounts section of the application.  

See API Authorization documentation for more info on OAuth2.

###### AWS SDK
Both web applications use the (Amazon) AWS SDK to access the S3 file system and EMR jobs. They use the accesskey and secretkey of the AWS account to authenticate.

###### Development

Recommended IDE is Netbeans. We use Maven to manage dependencies, compile and package the application. Eclipse with Maven plugins can be used too, but may cause more issues with Spring config files when attempting local Tomcat deployment.

install Java >=1.6 SDK.
install Netbeans or a similar framework.
install Tomcat7 or bigger (see e.g. http://wolfpaulus.com/jounal/mac/tomcat7/)
Configure Netbeans or the preferred framework to run webapps with Tomcat locally: http://technology.amis.nl/2012/01/02/installing-tomcat-7-and-configuring-as-server-in-netbeans/ To ensure proper operation, enter the following settings in the Tomcat Platform->VM options: -Xms512M -Xmx2048M -XX:MaxPermSize=1024m -XX:-UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled

###### How to import Maven project into Netbeans

Open or import object in Netbeans. It may probably be imported as an Eclipse project too.
Set “local” as default deployment profile: right-click project -> information -> maven -> enter “local” as active Maven profile. Check the settings in the local filter regarding the DB you are using (local or on development server), see below.

NOTE that to use the local profile you must have a MongoDB instance running locally on your computer with valid data (you can just clone the DEV database and import it into your local database). You must make sure your local DB has auth ON in its configuration file and it has correct data and an admin user created and a read_write user created whose password and username must match the ones configured in the API. See API and DB Server documentation for more info.
NOTE Make sure the port of DB and tomcat are the same as the ones in the configuration files of the API and the ADMIN tools
NOTE currently we use Amazon S3 for storage of images, make sure you have access to amazon with a valid user ID defined in the configuration file of Maven to be able to access. 

###### How to build and deploy

We use Maven profiles and environment filters to deploy to local,dev,prod environments with separate application properties (see pom.xml). The application properties files are in /src/main/filters:

application-local.properties
application-dev.properties
application-prod.properties

You have to define one of these profiles (local, dev, prod) when you build the application with Maven. You can select which by right clicking the project.

###### Build and deploy to server, e.g. for a release

*Right-click project -> Clean and build
*Make a copy of the generated .war file in the target folder (see in your file system) (most likely to be inside the *repository in the folder target).
*Alternatively you can use the Tomcat manager SERVER:8080/manager 
*You will need an admin account (check Server documentation for admin password in DEV and PROD)
*You will need the role manager-gui in the tomcat configuration
*Deploy this .war with the Tomcat manager 
*SSH into the server.
*Enter the webapps directory, e.g. /var/lib/tomcat7/webapps
*Stop the services, e.g. sudo service tomcat7 stop
*Delete a previous deployed folder if desired, and make a backup of its war file.
*Upload the new war file into this directory.
*Start the service, e.g. sudo service tomcat7 start


Note: We had Java heap space errors after re-deploying applications sometimes without restarting the services, although this might not be required in theory.


## Authorization Documentation

We use OAuth2 for authorization. See http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified for an introduction to OAuth2 and the different authorization flows. 

OAuth2 has the notion of authorization servers (handling authorization) and resource servers (delivering the content). We run authorization server and resource server in the same web application (called API). 

The API supports the OAuth2 password flow. This flow is used by all our applications at the moment. It means that the credentials to authenticate/authorize to the API are hard-coded in the applications. 

#### Authorization Request

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
