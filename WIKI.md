# Short title

Modernize a legacy application with digital decoupling

# Long title

Digital Decoupling Layer for Cloud Modernization using Cloud Pak for Data

# Author

* Balaji Kadambi (bkadambi@in.ibm.com)
* Shikha Maheswari (shikha.mah@in.ibm.com)
* Muralidhar Chavan (muralidhar.chavan@in.ibm.com)
* Manjula Hosurmath (mhosurma@in.ibm.com)
* Anil Paingankar (anpainga@in.ibm.com)

# URLs

### Github repo

* https://github.com/IBM/app-modernization-coexistence-layer

### Other URLs

Demo URL - https://youtu.be/Wrc3jFCJ7Yg

# Summary

Cloud Modernization at scale can be disruptive and clients prefer to start the journey that incrementally transition them to reach them to required digital maturity. This can be made possible by developing a co-existing layer that operates alongside core business application and database. Digital decoupling can also be seen as a coexistence layer that helps clients to incrementally tranistion to the future state with minimal or no disruption to the existing technology stack. There are numerous usecases that can be made possible using the decoupling layer, one such is the customer empowerment and smart front office application.

# Technologies

* Conversation
* Data management
* Databases
* Artificial Intelligence
* Machine Learning

# Description

In this code pattern, we will take the scenario of a telecom company that provides mobile network services. The company has a legacy application with a number of functional modules for customer information management, mobile plans management, inventory management and billing. The telecom company now wants to build a new system of engagement with an interactive chatbot for the customers. In the new chatbot the customers can query for billing information, data usage and also get plan recommendations. It is proposed to build this new chatbot using new technologies and capabilities of Cloud Pak for Data (CP4D) but without disrupting the existing legacy system. The legacy system uses a DB2 database and is the system of record. The new chatbot system uses a Postgresql database. A subset of data needed by the chatbot system is replicated to the PostgreSQL database using IBM DataStage.

When you have completed this code pattern, you will understand how to:
- Create DataStage flows for data replication using Cloud Pak for Data (CP4D)
- Create a chatbot using Watson Assistant
- Create Cloud functions in Watson Assistant to query databases

# Flow

![arch](images/architecture.png)

1. The employee performs one of many actions like create new mobile plan, enter new customer information or generate billing for customers.
2. The data is stored in the DB2 database.
3. Datastage reads data from DB2 database.
4. Datastage replicates the data in the PostgreSQL database.
5. A customer queries for billing information, data usage and recommendations through chatbot.
6. Watson Assistant chatbot invokes Cloud Functions for the queries.
7. Cloud functions queries PostgreSQL for data, processes the data and returns the response to Watson Assistant.


# Instructions

https://github.com/IBM/app-modernization-coexistence-layer#steps

# Components and services

* [IBM Cloud Pak for Data](https://developer.ibm.com/components/cloud-pak-for-data/)
* [Data Stage](https://cloud.ibm.com/catalog/services/datastage)
* [Java Liberty App](https://cloud.ibm.com/developer/appservice/starter-kits/687d91f2-ba5c-3914-8da5-57876c1f772a/java-liberty-app)
* [DB2](https://cloud.ibm.com/catalog/services/db2)
* [Watson Assistant](https://cloud.ibm.com/catalog/services/watson-assistant)
* [Databases for Postgresql](https://cloud.ibm.com/databases/databases-for-postgresql/create)
* [Functions](https://cloud.ibm.com/openwhisk)

# Runtimes

* Java
* Node


# Related IBM Developer content
* [Reasons to modernize your applications](https://developer.ibm.com/articles/reasons-to-modernize-your-applications)
* [Explore checkpoint and restart functionality in DataStage](https://developer.ibm.com/articles/explore-checkpoint-and-restart-functionality-in-datastage)
* [Perform advanced ETL operations with DataStage](https://developer.ibm.com/tutorials/perform-advanced-etl-operations-with-datastage)

# Related links




