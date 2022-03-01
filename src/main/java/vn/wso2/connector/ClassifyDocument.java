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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.synapse.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.services.comprehend.model.ClassifyDocumentRequest;
import com.amazonaws.services.comprehend.model.ClassifyDocumentResult;
import com.amazonaws.services.comprehend.model.DocumentClass;
import com.amazonaws.services.comprehend.model.DocumentLabel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

public class ClassifyDocument extends ComprehendAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyDocument.class);

    @Override
    protected void validateMandatoryParameter() {
        Assert.assertNotNull("The EndpointArn can't null", getParameterAsString("endpointArn"));
        Assert.assertNotNull("The Text can't null", getParameterAsString("text"));
    }

    @Override
    protected void execute(MessageContext messageContext) throws ConnectException {
        final long start = System.currentTimeMillis();

        final String endpointArn = getParameterAsString("endpointArn");
        final String text = getParameterAsString("text");

        final ClassifyDocumentRequest classifyDocumentRequest = new ClassifyDocumentRequest();
        classifyDocumentRequest.setText(text);
        classifyDocumentRequest.setEndpointArn(endpointArn);
        try {
            final ClassifyDocumentResult classifyDocumentResult = this.getComprehendClient().classifyDocument(
                classifyDocumentRequest);

            final Gson gson = new Gson();

            final JsonObject jsonObject = new JsonObject();

            final List<DocumentClass> documentClasses = Optional.ofNullable(classifyDocumentResult.getClasses()).orElse(
                new ArrayList<>());
            final JsonElement documentClassElement = gson.toJsonTree(documentClasses,
                new TypeToken<List<DocumentClass>>() {
                }.getType());
            final JsonArray documentClassAsJsonArray = documentClassElement.getAsJsonArray();

            final List<DocumentLabel> documentLabels = Optional.ofNullable(classifyDocumentResult.getLabels()).orElse(
                new ArrayList<>());
            final JsonElement documentLabelElement = gson.toJsonTree(documentLabels,
                new TypeToken<List<DocumentLabel>>() {
                }.getType());
            final JsonArray documentLabelAsJsonArray = documentLabelElement.getAsJsonArray();

            jsonObject.add("Classes", documentClassAsJsonArray);
            jsonObject.add("Labels", documentLabelAsJsonArray);

            messageContext.setProperty("classifyDocumentResult", jsonObject);
            LOGGER.info("Classify the document and put the response to classifyDocumentResult property. Took {} ms",
                System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new ConnectException(e, "Error while classifying the document. Detail: ");
        }
    }
}