in the jenkins web ui, go to dashboard -> manage jenkins -> plugins -> available plugins
search for "Deploy to container plugin" and "Maven Integration plugin" and install them.

navigate to Dashboard -> Manage Jenkins -> Credentials -> System -> Global credentials (unrestricted)
click on add credentials, add a username with password credentials with the username and password previously added to tomcat.

create a ssh public private key pair.
> by running
```
ssh-keygen -t rsa
```
navigate to Dashboard -> Manage Jenkins -> Credentials -> System -> Global credentials (unrestricted)
click on add credentials, add a SSH username with private key credential, use jenkins for username, select enter directly under Private Key, copy the private key just generated and paste it there, click on create.
add the public key to the github repository which has to be deployed. Go to the repository in your Github account -> Settings -> Deploy keys page. Paste the public key here.

click on add new item in the dashboard, select Freestyle project, provide a job name, click on ok.
under General, click on github project provide github project url
under Source Code Management, click on git provide the github project url, select the ssh key credential with username jenkins
under Build Triggers, select Poll SCM and enter `H * * * *`
under Build Environment, select Delete workspace before build starts
under Build Steps, add Invoke top-level Maven targets, in goals enter `clean compile package`, in POM under advanced enter "starter\_code/pom.xml"
under Post Build Actions, select Deploy war/ear to a container, enter "\*\*/\*.war" in WAR/EAR files, enter "ecom" in context path.
In containers add Tomcat 9.x Remote, select the username password credential for the tomcat in the credentials selection, enter the host and port (using private ip) of the tomcat container in the Tomcat URL.
save the job.

click on build now in your job view page. after completion of the process success status can be seen.
the job status can be also seen in the dashboard
