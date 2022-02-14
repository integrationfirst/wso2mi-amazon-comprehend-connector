/*
 * Class: ClassificationObject
 *
 * Created on Feb 14, 2022
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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.services.comprehend.model.ClassifyDocumentRequest;
import com.amazonaws.services.comprehend.model.ClassifyDocumentResult;
import com.google.gson.Gson;

import junit.framework.Assert;

public class ClassifyDocument extends ComprehendAgent {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GetObject.class);

    @Override
    protected void validateParameter() {
        Assert.assertNotNull("The EndpointArn can't null", getParameterAsString("endpointArn"));
        Assert.assertNotNull("The Text can't null", getParameterAsString("text"));
    }

    @Override
    protected void execute(MessageContext messageContext) throws ConnectException {
        final String endpointArn = getParameterAsString("endpointArn");
        final String text = getParameterAsString("text");

        final ClassifyDocumentRequest classifyDocumentRequest = new ClassifyDocumentRequest();
        classifyDocumentRequest.setText(text);
        classifyDocumentRequest.setEndpointArn(endpointArn);

        try {
            final ClassifyDocumentResult classifyDocumentResult = this.getComprehendClient().classifyDocument(
                classifyDocumentRequest);

            final String jsonAsString = new Gson().toJson(classifyDocumentResult);

            final JSONObject jsonObject = new JSONObject(jsonAsString);
            messageContext.getContextEntries().put("classifyDocumentResult", jsonObject);
        } catch (JSONException e) {
            LOGGER.error("", e);
        }
    }
}