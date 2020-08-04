# WSO2 API Manager 2.6 Custom Log Handler

wso2-apim-logHandler is sample handler which can used to log API transaction details in API Manager.

Please follow the steps below to configure the given jar in your environment.

1.) Download the Jar (wso2-APIM-logHandler/target/wso2-APIM3-logHandler-1.0-SNAPSHOT.jar)

2.) Copy Jar into <APIM_HOME>/repository/components/lib

3.) Add the following configuration to <APIM_HOME>/repository/conf/synapse-handlers.xml

```
<handlers>
	<handler name ="SESynapseLogHandler" class="com.wso2.apim.log.handler.SESynapseLogHandler"/>
</handlers>

```

4.) Add the following configurations to <APIM_HOME>/repository/conf/log4j.properties

```

log4j.logger.com.wso2.apim.log.handler.SESynapseLogHandler=DEBUG

```

5.) Start the server.

6.) When you invoke the API, you will be able to see the logs successfully.
