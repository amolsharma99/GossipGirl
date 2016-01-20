#Unavailable Now
Have Setup an EC2 instance with complete setup & dependencies installed

Public Ip - 54.109.101.182

download private key file 'rshiny.pem' to access machine from - http://bit.ly/privateKeyFile

save the file with permissions 400

`chmod 400 rshiny.pem`

Now you can log in using this file to the EC2 Instance.

`ssh -i rshiny.pem ubuntu@54.169.101.182`


All steps mentioned in usage manual are already executed here.

you can directly run the jar in GossipGirl/target and verify the functional correctness by performing operation on the mongo instance on the same machine alongside.

