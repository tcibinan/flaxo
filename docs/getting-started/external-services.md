# External services

Once you've registered you will be redirected to the courses page. 
Welcoming notification will also inform you that registration's finished successfully.

![home-page](../images/registration-successful.png)

Courses page contains all the course that you've created. 
By now it is empty, of course. Nevertheless once created  course will always be on the courses page. 
It can be accessed from most of the Flaxo pages using the **Courses** button in the page header. 
Courses page also contains **Create course** button which is disabled now. 
It is disabled because you have to authorize with several *external services* first.

In terms of Flaxo external service is an integrated service that the platform uses to host courses or validate
solutions.
All the external services should be connected to Flaxo using different authorization processes.

To see a full list of external services click **Services** button in the page header.
Services dropdown will appear.

![services-dropdown-non-authorized](../images/services-dropdown-non-authorized.png)

You can see that *GitHub* and *Codacy* external services have associated yellow circles in the services dropdown. 
At the same time *Travis* doesn't have one. 
Basically, it means that *GitHub* and *Codacy* services require additional actions from you to connect them to Flaxo 
and *Travis* doesn't.

## GitHub

*GitHub* is a web-based hosting service for version control using Git. It is completely free for public use. And for 
the moment *GitHub* is the only Git service that Flaxo supports. 

You should have a *GitHub* account to authorize it in Flaxo.
To connect your *GitHub* account to your Flaxo account click on the **GitHub** in the services dropdown.
*GitHub* settings popup will appear.

![github-authorization-popup](../images/github-authorization-popup.png)

Click **Sign in with GitHub** to initiate an OAuth authorization process. You will be redirected several times and
after the authorization confirmation you will be redirected back to Flaxo application.

If you open the services dropdown again you will see that *GitHub* has lost its yellow circle.

![services-dropdown-github-authorized](../images/services-dropdown-github-authorized.png)

And if you click on **GitHub** in the services dropdown again you will see that you are already authorized with 
your *GitHub* account.
By the way there is a **Logout from GitHub** button which doesn't work in the current release.

![github-authorization-popup-authorized](../images/github-authorization-popup-authorized.png)

## Codacy

*Codacy* is a web-based static analysis service which encapsulates a lot of static analysis tools. It performs an
automated code quality analysis and produces a unified overall code quality summary.

You should be authorized in *Codacy* with your *GitHub* account to authorize it in Flaxo.
To connect your *Codacy* account to your Flaxo account click on the **Codacy** in the services dropdown.
*Codacy* settings popup will appear.

![codacy-authorization-popup](../images/codacy-authorization-popup.png)

Click with the middle mouse button on the **codacy account settings** link in the *Codacy* settings popup to open
your *Codacy* account settings in a new browser tab. Go to the new tab and copy the generated API token.

![codacy-api-token](../images/codacy-api-token.png)

Turn back to the Flaxo browser tab, put copied API token in the **Codacy token** field and click 
**Update codacy token**.
The notification declaring that API token was added to your account should appear.

![codacy-authorization-popup-successful](../images/codacy-authorization-popup-successful.png)

Now if you refresh the page and open services dropdown you'll see that *Codacy* has lost its yellow circle.

![services-dropdown-all-authorized](../images/services-dropdown-all-authorized.png)

And if you click on **Codacy** in the services dropdown again you will see that you are already authorized with 
your *Codacy* account.

![codacy-authorization-popup-authorized](../images/codacy-authorization-popup-authorized.png)

Note that you can update your *Codacy* API token at any time using the *Codacy* settings popup.

## Travis

*Travis* is a web-based continuous integration service.
It can run tests in repository branches and pull requests.

You should be authorized in *Travis* with your *GitHub* account to authorize it in Flaxo.
No other additional actions are required because Flaxo connects your *Travis* account using your *GitHub* 
authorization.
