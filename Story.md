This is working based on oplog,

1. oplog used for replication in mongo us present in db.local.oplog.rs() collection. this is a capped collection
 and hence tailed cursor can be obtained on this (Link). Using this tailed cursor we will push all the data coming
 our way to a rabbitmq.

2. A worker/consumer will be consuming in realtime from the above queue and process the received messages as described
in the step below.

3. Message received above will be validated if it of interest for any client, if yes then will be sent as per mechanism
specified by the client.

4...
