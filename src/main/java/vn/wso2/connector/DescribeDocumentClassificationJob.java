/*
 * Class: DescribeDocumentClassificationJob
 *
 * Created on Feb 15, 2022
 *
 * (c) Copyright Swiss Post Solutions Ltd, unpublished work
 * All use, disclosure, and/or reproduction of this material is prohibited
 * unless authorized in writing.  All Rights Reserved.
 * Rights in this program belong to:
 * Swiss Post Solution.
 * Floor 4-5-8, ICT Tower, Quang Trung Software City
 */
package vn.wso2.connector;

import org.apache.synapse.MessageContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.services.comprehend.model.DescribeDocumentClassificationJobRequest;
import com.amazonaws.services.comprehend.model.DescribeDocumentClassificationJobResult;
import com.google.gson.Gson;

import junit.framework.Assert;

public class DescribeDocumentClassificationJob extends ComprehendAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeDocumentClassificationJob.class);

    @Override
    protected void validateMandatoryParameter() {
        Assert.assertNotNull("The jobId can't null", getParameterAsString("jobId"));
    }

    @Override
    protected void execute(MessageContext messageContext) throws ConnectException {
        final long start = System.currentTimeMillis();

        final String jobId = getParameterAsString("jobId");

        final DescribeDocumentClassificationJobRequest describeDocumentClassificationJobRequest = new DescribeDocumentClassificationJobRequest();
        describeDocumentClassificationJobRequest.setJobId(jobId);
        try {
            final DescribeDocumentClassificationJobResult describeDocumentClassificationJobResult = getComprehendClient().describeDocumentClassificationJob(
                describeDocumentClassificationJobRequest);

            final String jsonAsString = new Gson().toJson(describeDocumentClassificationJobResult);

            final JSONObject jsonObject = new JSONObject(jsonAsString);
            messageContext.setProperty("classificationJobResult", jsonObject);

            LOGGER.debug(
                "Describe the document classification job and put the response to classificationJobResult property. Took {} ms",
                System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("Error while describing the document. Detail: ", e);
        }
    }

}