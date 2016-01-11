Dependencies -
mongo setup with replica set enabled, even single node replica setup will work.
rabbitmq 3.6
Java 7

Steps to be followed on a debian linux machines -

1. Install Java 7 -

        sudo add-apt-repository ppa:webupd8team/java
        sudo apt-get update && sudo apt-get install oracle-jdk7-installer
        
2. Install maven -

        sudo apt-get install maven

3. Install git -
 

        sudo apt-get install git

4. Install scala

        sudo apt-get install scala

5. Install RabbitMq

        https://www.rabbitmq.com/install-debian.html
        
6. Install MongoDB

        https://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/        

7. Enable Replica setup

        #stop running instance
        sudo service mongod stop
        #start new instance with --replSet
        sudo mkdir -p /data/db
        sudo chown -R $USER_NAME /data/db
        mongod --replSet myDevReplSet &
        #connect to mongo
        mongo
        >rs.initiate()
        #you can now start doing operations in mongo and should see oplog getting populated.

8. Clone project in local dir 

        git clone https://github.com/amolsharma99/GossipGirl.git

9. Build & Run Project 

        cd $PROJECT_DIR
        mvn clean install
        ##created shaded uber jar
        mvn package 
        ##execute jar
        java -jar target/SocialCops-1.0-SNAPSHOT.jar 

jars/libraries used (versions mentioned in pom)

scala version 2.11.6 (handled in pom)

play 2.3.x (handled in pom)

reactivemongo scala mongo driver
