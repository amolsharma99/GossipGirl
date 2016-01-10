I divided the problem into two parts 
   
    A. getting notifications from db on every change to my service.
    
    B. filtering/processing and sending notification on relevant change. 
    
Second part was straight forward, i am getting changes from somewhere as a stream, then i can filter them and
send to the relevant clients.

For first part, i was not sure what could be the best approach. i was expecting there was some provision in mongoDB
which will let you subscribe to the changes happening in DB. In SQL systems people do the same thing with message brokers.

Found that enterprise version supports auditing, but ofcourse for solving challenges we are not allowed to use 
enterprise/paid solutions so didn't try to explore it and moved on to look for alternative. 
Ref - https://docs.mongodb.org/v3.0/core/auditing/

After came across a blog https://appinno.wordpress.com/2012/03/29/mongodb-notification-using-zeromq/.
This guy did something good, he was using a custom mongo build and pushing changes from mongo to zmq in realtime.
since this was a custom build and his pull request(https://github.com/mongodb/mongo/pull/199) is rejected to be merged 
into main, it would be risky to use it considering reliability and maintainability in future.

next thing to be tried was oplog.
In the process of understanding previous soln with the help of mongo-user group i got a good hint to approach further 
from Stephen Steneker from mongodb group. discussion link -
https://groups.google.com/forum/?utm_medium=email&utm_source=footer#!msg/mongodb-user/bCyPZ5JDxCM/Zikhc3gDDgAJ
After his suggestion of use tailable cursor with oplog and finding a blog on compose.io where oplog tailed cursor 
was demonstrated with oplog. oplog seemed to be the way to go.

mongo-connector (https://github.com/mongodb-labs/mongo-connector) was also considered but was not very confident with it,
as anyway had to write docManager API implementation, since currently support mongoDB, solr, elastic search. 
didn't wanted to again use mongo as queue. work was pretty much comparable in going with this or oplog soln. 
In addition disclaimer on their github page was not very encouraging though it seems to be widely used.

This is work based on oplog,

1. oplog used for replication in mongo us present in db.local.oplog.rs() collection. this is a capped collection
 and hence tailed cursor can be obtained on this (Link). Using this tailed cursor we will push all the data coming
 our way to a rabbitmq.

2. A worker/consumer will be consuming in realtime from the above queue and process the received messages as described
in the step below.

3. Message received above will be validated if it of interest for any client, if yes then will be sent as per mechanism
specified by the client.



4...
