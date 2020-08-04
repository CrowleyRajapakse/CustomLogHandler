package com.wso2.apim.log.handler;

import org.apache.axis2.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.rest.RESTConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SESynapseLogHandler extends AbstractSynapseHandler {

    // Logger instance
    private static final Log LOGGER = LogFactory.getLog(SESynapseLogHandler.class);

    private static final String SELOG_INFLOW_REQUEST_START_TIME = "SELOG_INFLOW_REQUEST_START_TIME";
    private static final String SELOG_OUTFLOW_REQUEST_START_TIME = "SELOG_OUTFLOW_REQUEST_START_TIME";
    private static final String SELOG_INFLOW_RESPONSE_END_TIME = "SELOG_INFLOW_RESPONSE_END_TIME";

    /*
     * Incoming request to the service or API. This is the first entry point,
     * in fact it is after Axis2 layer. This is where we will determine the
     * tracking id and log HTTP method and headers similar to wire log.
     */
    public boolean handleRequestInFlow(MessageContext synCtx) {
        synCtx.setProperty(SELOG_INFLOW_REQUEST_START_TIME, System.currentTimeMillis());
        return true;           // always return true
    }

    /*
     * Outgoing request from the service to the backend. This is where we will
     * log the outgoing HTTP address and headers.
     */
    public boolean handleRequestOutFlow(MessageContext synCtx) {
        synCtx.setProperty(SELOG_OUTFLOW_REQUEST_START_TIME, System.currentTimeMillis());
        return true;           // always return true
    }

    /*
     * Incoming response from backend to service. This is where we will
     * log the backend response headers and status.
     */
    public boolean handleResponseInFlow(MessageContext synCtx) {
        synCtx.setProperty(SELOG_INFLOW_RESPONSE_END_TIME, System.currentTimeMillis());
        return true;           // always return true
    }

    /*
     * Outgoing response from the service to caller. This is where we will log
     * the service response header and status.
     */
    public boolean handleResponseOutFlow(MessageContext synCtx) {

        SynapseLog log = SELogTrackUtil.getLog(synCtx, LOGGER);
        long responseTime, serviceTime = 0, backendTime = 0, backendEndTime = 0;
        String startTimeFormatted = "";
        long endTime = System.currentTimeMillis();
        DateFormat simple = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS Z");
        String endTimeFormatted = simple.format(new Date(endTime));
        try {
            // Set the logging context
            SELogTrackUtil.setLogContext(synCtx, log);
            long startTime = 0, backendStartTime = 0;
            if (synCtx.getProperty(SELOG_INFLOW_REQUEST_START_TIME) != null) {
                startTime = (Long) synCtx.getProperty(SELOG_INFLOW_REQUEST_START_TIME);
                startTimeFormatted = simple.format(new Date(startTime));
            }

            if (synCtx.getProperty(SELOG_OUTFLOW_REQUEST_START_TIME) != null) {
                backendStartTime = (Long) synCtx.getProperty(SELOG_OUTFLOW_REQUEST_START_TIME);
            }

            if (synCtx.getProperty(SELOG_INFLOW_RESPONSE_END_TIME) != null) {
                backendEndTime = (Long)synCtx.getProperty(SELOG_INFLOW_RESPONSE_END_TIME);
            }

            responseTime = endTime - startTime;
            //When start time not properly set
            if (startTime == 0) {
                backendTime = 0;
                serviceTime = 0;
            } else if (endTime != 0 && backendStartTime != 0 && backendEndTime != 0) { //When
                // response caching is disabled
                backendTime = backendEndTime - backendStartTime;
                serviceTime = responseTime - backendTime;
            } else if (endTime != 0 && backendStartTime == 0) {//When response caching enabled
                backendTime = 0;
                serviceTime = responseTime;
            }

            String API_NAME = (String) synCtx.getProperty(RESTConstants.SYNAPSE_REST_API);
            String HTTP_METHOD = (String) synCtx.getProperty(Constants.Configuration.HTTP_METHOD);
            String CONTEXT = (String) synCtx.getProperty(RESTConstants.REST_API_CONTEXT);
            String FULL_REQUEST_PATH = (String) synCtx.getProperty(RESTConstants.REST_FULL_REQUEST_PATH);
            String SUB_PATH = (String) synCtx.getProperty(RESTConstants.REST_SUB_REQUEST_PATH);
            String HTTP_RESPONSE_STATUS_CODE = SELogTrackUtil.getHTTPStatusMessage(synCtx);
            String ERROR_CODE = String.valueOf(synCtx.getProperty(SynapseConstants.ERROR_CODE));
            String ERROR_MESSAGE = (String) synCtx.getProperty(SynapseConstants.ERROR_MESSAGE);

            log.auditLog("API Transaction Details:"
                    + "SERVICE_NAME: " + API_NAME + ",HTTP_METHOD: " + HTTP_METHOD + ", CONTEXT: " + CONTEXT +
                    ",FULL_REQUEST_PATH" + FULL_REQUEST_PATH + ",SUB_PATH: " + SUB_PATH +
                    ", HTTP_RESPONSE_STATUS_CODE: " + HTTP_RESPONSE_STATUS_CODE + ", REQUEST_START_TIME: "+ startTimeFormatted
                    + ", RESPONSE_END_TIME: " + endTimeFormatted + ", REQUEST_DURATION: " + responseTime +
                    ", BACKEND_TIME: " + backendTime + ", SERVICE_TIME: " + serviceTime +
                    ", ERROR_CODE: " + ERROR_CODE + ", ERROR_MESSAGE: " + ERROR_MESSAGE);

        } catch (Exception e) {
            log.auditWarn("Unable to set log context due to : " + e.getMessage());
        }

        return true;           // always return true
    }
}
