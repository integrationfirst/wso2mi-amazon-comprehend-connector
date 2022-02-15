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

import org.apache.synapse.MessageContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.services.comprehend.model.InputDataConfig;
import com.amazonaws.services.comprehend.model.OutputDataConfig;
import com.amazonaws.services.comprehend.model.StartDocumentClassificationJobRequest;
import com.amazonaws.services.comprehend.model.StartDocumentClassificationJobResult;
import com.google.gson.Gson;

public class StartDocumentClassificationJob extends ComprehendAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartDocumentClassificationJob.class);

    @Override
    protected void validateMandatoryParameter() {

    }

    @Override
    protected void execute(MessageContext messageContext) throws ConnectException {
        final long start = System.currentTimeMillis();

        final String clientRequestToken = getParameterAsString("clientRequestToken");
        final String dataAccessRoleArn = getParameterAsString("dataAccessRoleArn");
        final String documentClassifierArn = getParameterAsString("documentClassifierArn");
        final String documentReadAction = getParameterAsString("documentReadAction");
        final String documentReadMode = getParameterAsString("documentReadMode");
        //        final String featureTypes = getParameterAsString("featureTypes");
        final String inputFormat = getParameterAsString("inputFormat");
        final String inputS3Uri = getParameterAsString("inputS3Uri");
        final String jobName = getParameterAsString("jobName");
        final String kmsKeyId = getParameterAsString("kmsKeyId");
        final String outputS3Uri = getParameterAsString("outputS3Uri");
        final String volumeKmsKeyId = getParameterAsString("volumeKmsKeyId");
        //        final String securityGroupIds = getParameterAsString("securityGroupIds");
        //        final String subnets = getParameterAsString("subnets");

        final InputDataConfig inputDataConfig = new InputDataConfig();
        inputDataConfig.setS3Uri(inputS3Uri);

        final OutputDataConfig outputDataConfig = new OutputDataConfig();
        outputDataConfig.setS3Uri(outputS3Uri);
        outputDataConfig.setKmsKeyId(kmsKeyId);

        final StartDocumentClassificationJobRequest classificationJobRequest = new StartDocumentClassificationJobRequest();

        classificationJobRequest.setJobName(jobName);
        classificationJobRequest.setDocumentClassifierArn(documentClassifierArn);
        classificationJobRequest.setDataAccessRoleArn(dataAccessRoleArn);
        classificationJobRequest.setInputDataConfig(inputDataConfig);
        classificationJobRequest.setOutputDataConfig(outputDataConfig);
        classificationJobRequest.setVolumeKmsKeyId(volumeKmsKeyId);

        try {
            final StartDocumentClassificationJobResult classificationJobResult = getComprehendClient().startDocumentClassificationJob(
                classificationJobRequest);

            final String jsonAsString = new Gson().toJson(classificationJobResult);

            final JSONObject jsonObject = new JSONObject(jsonAsString);
            messageContext.getContextEntries().put("classificationJobResult", jsonObject);

            LOGGER.debug("Start the document classification job. Took {} ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("Error while starting the document. Detail: ", e);
        }
    }

}