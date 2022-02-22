/*
 * Class: StartDocumentClassificationJob
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

import java.util.ArrayList;
import java.util.List;

import org.apache.synapse.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.services.comprehend.model.DocumentReaderConfig;
import com.amazonaws.services.comprehend.model.InputDataConfig;
import com.amazonaws.services.comprehend.model.OutputDataConfig;
import com.amazonaws.services.comprehend.model.StartDocumentClassificationJobRequest;
import com.amazonaws.services.comprehend.model.StartDocumentClassificationJobResult;
import com.amazonaws.services.comprehend.model.Tag;
import com.amazonaws.services.comprehend.model.VpcConfig;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import junit.framework.Assert;

public class StartDocumentClassificationJob extends ComprehendAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartDocumentClassificationJob.class);

    @Override
    protected void validateMandatoryParameter() {
        Assert.assertNotNull("The dataAccessRoleArn can't null", getParameterAsString("dataAccessRoleArn"));
        Assert.assertNotNull("The documentClassifierArn can't null", getParameterAsString("documentClassifierArn"));
        Assert.assertNotNull("The inputS3Uri can't null", getParameterAsString("inputS3Uri"));
        Assert.assertNotNull("The jobName can't null", getParameterAsString("jobName"));
        Assert.assertNotNull("The outputS3Uri can't null", getParameterAsString("outputS3Uri"));
    }

    @Override
    protected void execute(MessageContext messageContext) throws ConnectException {
        final long start = System.currentTimeMillis();
        final String dataAccessRoleArn = getParameterAsString("dataAccessRoleArn");
        final String documentClassifierArn = getParameterAsString("documentClassifierArn");
        final String jobName = getParameterAsString("jobName");
        try {

            final InputDataConfig inputDataConfig = this.createInputDataConfig();

            final OutputDataConfig outputDataConfig = this.createOutputDataConfig();

            final StartDocumentClassificationJobRequest classificationJobRequest = new StartDocumentClassificationJobRequest();

            classificationJobRequest.setJobName(jobName);
            classificationJobRequest.setDocumentClassifierArn(documentClassifierArn);
            classificationJobRequest.setDataAccessRoleArn(dataAccessRoleArn);
            classificationJobRequest.setInputDataConfig(inputDataConfig);
            classificationJobRequest.setOutputDataConfig(outputDataConfig);

            this.setOptionalProperties(classificationJobRequest);

            LOGGER.info("Prepared classification job request.");

            final StartDocumentClassificationJobResult classificationJobResult = getComprehendClient().startDocumentClassificationJob(
                classificationJobRequest);

            final JsonElement jsonElement = new Gson().toJsonTree(classificationJobResult);
            messageContext.setProperty("classificationJobResult", jsonElement);

            LOGGER.info(
                "Start the document classification job and put the response to classificationJobResult property. Took {} ms",
                System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new ConnectException(e, "Error while starting the document. Detail: ");
        }
    }

    @SuppressWarnings("unchecked")
    private void setOptionalProperties(StartDocumentClassificationJobRequest classificationJobRequest) {
        final String clientRequestToken = getParameterAsString("clientRequestToken");
        final String documentReadAction = getParameterAsString("documentReadAction");
        final String documentReadMode = getParameterAsString("documentReadMode");
        final String featureTypesAsString = getParameterAsString("featureTypes");
        final String tagsAsString = getParameterAsString("tags");
        final String inputFormat = getParameterAsString("inputFormat");
        final String kmsKeyId = getParameterAsString("kmsKeyId");
        final String volumeKmsKeyId = getParameterAsString("volumeKmsKeyId");
        final List<String> securityGroupIds = getParameter("securityGroupIds", List.class);
        final List<String> subnets = getParameter("subnets", List.class);

        if (StringUtils.hasValue(clientRequestToken)) {
            classificationJobRequest.setClientRequestToken(clientRequestToken);
        }
        if (StringUtils.hasValue(inputFormat)) {
            classificationJobRequest.getInputDataConfig().setInputFormat(inputFormat);
        }
        if (StringUtils.hasValue(kmsKeyId)) {
            classificationJobRequest.getOutputDataConfig().setKmsKeyId(kmsKeyId);
        }
        if (StringUtils.hasValue(volumeKmsKeyId)) {
            classificationJobRequest.setVolumeKmsKeyId(volumeKmsKeyId);
        }
        setDocumentReaderConfig(classificationJobRequest, documentReadAction, documentReadMode, featureTypesAsString);
        if (StringUtils.hasValue(tagsAsString)) {
            List<Tag> tags = new GsonBuilder().create().fromJson(tagsAsString, ArrayList.class);
            classificationJobRequest.setTags(tags);
        }
        setVpcConfig(classificationJobRequest, securityGroupIds, subnets);
    }

    private void setVpcConfig(
        StartDocumentClassificationJobRequest classificationJobRequest,
        final List<String> securityGroupIds,
        final List<String> subnets) {
        final VpcConfig vpcConfig = new VpcConfig();

        if (!CollectionUtils.isNullOrEmpty(securityGroupIds)) {
            vpcConfig.setSecurityGroupIds(securityGroupIds);
        }
        if (!CollectionUtils.isNullOrEmpty(subnets)) {
            vpcConfig.setSubnets(subnets);
        }
        if (!CollectionUtils.isNullOrEmpty(securityGroupIds) || !CollectionUtils.isNullOrEmpty(subnets)) {
            classificationJobRequest.setVpcConfig(vpcConfig);
        }
    }

    @SuppressWarnings("unchecked")
    private void setDocumentReaderConfig(
        final StartDocumentClassificationJobRequest classificationJobRequest,
        final String documentReadAction,
        final String documentReadMode,
        final String featureTypesAsString) {

        final DocumentReaderConfig documentReaderConfig = new DocumentReaderConfig();
        if (StringUtils.hasValue(documentReadAction)) {
            documentReaderConfig.setDocumentReadAction(documentReadAction);
        }
        if (StringUtils.hasValue(documentReadMode)) {
            documentReaderConfig.setDocumentReadMode(documentReadMode);
        }

        List<String> featureTypes = null;
        if (StringUtils.hasValue(featureTypesAsString)) {
            featureTypes = new GsonBuilder().create().fromJson(featureTypesAsString, ArrayList.class);
            documentReaderConfig.setFeatureTypes(featureTypes);
        }
        if (StringUtils.hasValue(documentReadAction) || StringUtils.hasValue(documentReadMode)
                || !CollectionUtils.isNullOrEmpty(featureTypes)) {
            classificationJobRequest.getInputDataConfig().setDocumentReaderConfig(documentReaderConfig);
        }
    }

    private InputDataConfig createInputDataConfig() {
        final String inputS3Uri = getParameterAsString("inputS3Uri");

        final InputDataConfig inputDataConfig = new InputDataConfig();
        inputDataConfig.setS3Uri(inputS3Uri);

        return inputDataConfig;
    }

    private OutputDataConfig createOutputDataConfig() {
        final String outputS3Uri = getParameterAsString("outputS3Uri");

        final OutputDataConfig outputDataConfig = new OutputDataConfig();
        outputDataConfig.setS3Uri(outputS3Uri);

        return outputDataConfig;
    }
}