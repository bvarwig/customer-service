# Persephone Customer

This project is part of the [Persephone Project](https://github.wdf.sap.corp/SolutionPioneering/persephone) and provides a OData based Customer service 
filtering the business partners from an S4 Backed system. 

Find more details in the [SAP Jam Project "Persephone"](https://jam4.sapjam.com/groups/O1GnGq1JBbvajL09zJKI1V/overview_page/PUP3uP5Unsv1GlGiwZEU2j)

# Table of Contend

1. [Setup](#1-setup)
2. [Steps to get Sources from Scratch using Web IDE](#2-steps-to-get-sources-from-scratch-using-web-ide)
3. [How to Build & Deploy](#3-how-to-build--deploy)
4. [The Application *customersrv*](#4-the-application-customersrv)

# 1. Setup


##  Pre-Requisites
Follow the [Chapter Setup of Persephone "Step By Step Guidance"](https://github.wdf.sap.corp/SolutionPioneering/persephone/blob/master/StepByStepGuidance.md#1-setup)

Result: 
* Web IDE configured and build installed in your space. 
* The following Service instances are defined in your CF space:
    * xsuaa
	* dest-service

## Create Destination *S4HANA_basic*

You have to define a Destination to your s4 backend

1. Open your CF Space
2. On **Subaccount** level select **Connectiviry > Destinations > New Destination** 
3. Create a new Destination with the following properties or import from [imports/destinations/S4HANA_basic.properties](imports/destinations/S4HANA_basic.properties):
   * Name=**S4HANA_basic**
   * Type=**HTTP**
   * URL=**https://my300497-api.s4hana.ondemand.com**
   * ProxyType=**Internet**
   * Authentication=**BasicAuthentication**
   * User=**SAPCP_COMM_USER**
   * Password=... find this user at https://wiki.wdf.sap.corp/wiki/display/sp/System+Passwords
   

# 2. Steps to get Sources from Scratch using Web IDE 

## Creating Project Stub via Template

1. Open the Web IDE
2. In case you have not opened the **Development** do this by selecting the **</>** at the left side.
3. Open the Context Menu of **Workspace** and select
   **New > Project from Template > SAP Cloud Platfrom Business Application**.
   Use the following values and for all others the defaults are fine: 
   * Base Information > Project Name = **persephonecustomer**
   * Template Customization > Java Module Settings Package = **com.sap.solpio.persephonecustomer**

Result: a new Project in created in your workspace.
   
## Configure Modules and Resource

### Update the *mta.yml*

1. Copy the sources from [mta.yaml](mta.yaml)
2. [optional] delete the not needed folder of module **db**.

Result: 
* The module **DB** is no longer used, as not needed for this project
* module **srv** is renamed to **customersrv** to have a unique name, when deploying a second 
  template generated project in same target CF space.
* needed resources are added:
   ```
   resources:
     - name: dest-service
       type: org.cloudfoundry.existing-service
     - name: cap-uaa
       type: org.cloudfoundry.existing-service	   
   ```
* requirements for the java module are added: 
   ```
   modules:
     - name: customersrv
	 
             [...]
			 
       requires:
         - name: dest-service
         - name: cap-uaa
	```


###	[optional] update manifest.yml

In case you want to have the manifest in sync with the **mta.yml**  do:

1. update this file with [srv/manifest.yml](srv/manifest.yml):


## Implement Customer Service

### my-service.cds

1. Copy sources from [srv/my-service.cds](srv/my-service.cds)

### Create Customer Class 

1. Check in the **srv/pom.xml** which **project.properties.packageName** is used, 
   as you have to locale the Customer class inside this package. 
   Via Template generation you decided for this package name. 
2. In folder **srv/src** via context menu **NEW > JavaClass**:
   * package:  **com.sap.solpio.persephonecustomer.model**
   * Class name: **Customer** 
3. Paste the code from [srv/src/main/java/com/sap/solpio/persephonecustomer/model/Customer.java](srv/src/main/java/com/sap/solpio/persephonecustomer/model/Customer.java) and save.

### Create CustomerHandler class

1. In folder **srv/src** via context menu **NEW > JavaClass**:
   * package:  **com.sap.solpio.persephonecustomer.customerhandler**
   * Class name: **CustomerHandler** 
2. Paste the code from [srv/src/main/java/com/sap/solpio/persephonecustomer/customerhandler/CustomerHandler.java](srv/src/main/java/com/sap/solpio/persephonecustomer/customerhandler/CustomerHandler.java) and save.

# 3. How to Build & Deploy

## Build mta

Pre-Requisites: Project **persephonecustomer** exist iny our Web IDE.

1. Via Context Menu of the Project **persephonecustomer** run **Build**. 
2. Wait until an successful build info pops up.
3. If you can't find the resulting **/mta_archives/persephonecustomer/persephonecustomer_0.0.1.mtar**
   call **refresh Workspace** or refresch browser. 

## Deployed to CF Space

1. Select the just build **persephonecustomer_0.0.1.mtar** and run **Deploy** via context Menu. 
   
# 4. The Application *customersrv*

1. Open CF Space in Browser
2. Find new application **customersrv**  and open its **Applicaton Routes**
3. Open the **CustomerService**
4. Check if you can see Customers using  `https://{{baseurl}}/odata/v2/CustomerService/Customer`

