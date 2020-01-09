# WSO2 API Manager 3.0 Custom Log Handler

wso2-apim3-logHandler is sample handler which can used to log API transaction details in API Manager.

Please follow the steps below to configure the given jar in your environment.

1) Download the Jar (wso2-APIM-logHandler/target/wso2-APIM3-logHandler-1.0-SNAPSHOT.jar)
2) Copy Jar into <APIM_HOME>/repository/components/lib
3) Add the following configuration to <APIM_HOME>/repository/conf/deployment.toml
```
enabled_global_handlers= ["custom_logger"]
[synapse_handlers]
custom_logger.name= "SESynapseLogHandler"
custom_logger.class= "com.wso2.apim.log.handler.SESynapseLogHandler"

```
4) Add the following configurations to <APIM_HOME>/repository/conf/log4j.properties
```

loggers = <Existing Loggers>, com.wso2.apim.log.handler.SESynapseLogHandler
logger.com.wso2.apim.log.handler.SESynapseLogHandler.name=com.wso2.apim.log.handler.SESynapseLogHandler
logger.com.wso2.apim.log.handler.SESynapseLogHandler.level=DEBUG
logger.com.wso2.apim.log.handler.SESynapseLogHandler.appenderRef.CARBON_LOGFILE.ref = CARBON_LOGFILE

```
5) Start the server.
6) Check the following configurations is in the <APIM_HOME>/repository/conf/synapse-handlers.xml file
```
<handlers>
	<handler name ="SESynapseLogHandler" class="com.wso2.apim.log.handler.SESynapseLogHandler"/>
</handlers>

```
7) When you invoke the API, you will be able to see the logs successfully.
