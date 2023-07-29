**Install Plugins**
- In the jenkins web ui, go to dashboard -> manage jenkins -> plugins -> available plugins
- Search for "Deploy to container plugin" and "Maven Integration plugin" and install them.

**Add Credentials**
- Navigate to Dashboard -> Manage Jenkins -> Credentials -> System -> Global credentials (unrestricted)
- Click on add credentials, add a username with password credentials with the username and password previously added to tomcat.

![Username with Password Credential](images/cicd/creating-username-with-password-credential.png)

- Create a ssh public private key pair.
> By running
```
ssh-keygen -t rsa
```
- Navigate to Dashboard -> Manage Jenkins -> Credentials -> System -> Global credentials (unrestricted)
- Click on add credentials, add a SSH username with private key credential, use jenkins for username, select enter directly under Private Key, copy the private key just generated and paste it there, click on create.

![Username with SSH key credential](images/cicd/creating-username-with-sshkey-credential-2.png)

- Add the public key to the github repository which has to be deployed. Go to the repository in your Github account -> Settings -> Deploy keys page. Paste the public key here.

![Add Deployment Key to github repository](images/cicd/adding-deploymentkey-to-github-repository.png)
![Credential listing](images/cicd/credentials-list.png)

**Creating Job**
- Click on add new item in the dashboard, select Freestyle project, provide a job name, click on ok.
- Under General, click on github project provide github project url.

![Step 1](images/cicd/configure-job-1.png)

- Under Source Code Management, click on git provide the github project url, select the ssh key credential with username 'jenkins'.

![Step 2](images/cicd/configure-job-2.png)

- Under Build Triggers, select Poll SCM and enter `H * * * *`.

![Step 3](images/cicd/configure-job-3.png)

- Under Build Environment, select Delete workspace before build starts.

![Step 4](images/cicd/configure-job-4.png)

- Under Build Steps, add Invoke top-level Maven targets, in goals enter `clean compile package`, in POM under advanced enter "starter\_code/pom.xml".

![Step 5](images/cicd/configure-job-5.png)

- Under Post Build Actions, select Deploy war/ear to a container, enter "\*\*/\*.war" in WAR/EAR files, enter "ecom" in context path.

![Step 6](images/cicd/configure-job-6.png)

- In containers add Tomcat 9.x Remote, select the username password credential for the tomcat in the credentials selection, enter the host and port (using private ip) of the tomcat container in the Tomcat URL.

![Step 7](images/cicd/configure-job-7.png)
![Step 8](images/cicd/configure-job-8.png)

- Save the job.

**Building Testing And Deploying**
- Click on build now in your job view page. After completion of the process success status can be seen.
- The job status can be also seen in the dashboard.

![Console Output 1](images/cicd/job-success-console-output-1.png)
![Console Output 2](images/cicd/job-success-console-output-2.png)
![Job Status Main](images/cicd/job-status-main-page.png)
![Job Status Listing](images/cicd/job-status-listing.png)

- The application runs successfully.

![Application Run Success 1](images/cicd/application-run-success-1.png)
![Application Run Success 2](images/cicd/application-run-success-2.png)
