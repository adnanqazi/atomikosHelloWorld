# atomikosHelloWorld
Two DB instances, both H2. Their datasources are created and wrapped in XADataSourceWrapper. 
Spring boot's default datasource initializer is disabled (look at application.properties). Two custom beans initialize each data source. Note how aInit and bInit are injected with their specific dataSource

Usage: 
Post the following JSON to http://localhost:8080/
{"name": "magpie"
}

Verify Submitted values with GET
http://localhost:8080/messages
http://localhost:8080/pets
Repeat the process choosing some other animal in JSON

Now add rollback query param to http://localhost:8080?rollback=e
Note, checking with above GET calls, payload was not commited, despite that fact that exception is thrown after after DB calls

Unit test part needs to be driven to completion, perhaps a couple of hrs of my time, when i have it, the runtime logic however runs end to end, even in current shape 
