package vn.wso2.connector;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;

import junit.framework.Assert;

public abstract class ComprehendAgent extends AbstractConnector {

    private MessageContext context;

    private AmazonComprehend comprehendClient;

    @Override
    public final void connect(final MessageContext messageContext) throws ConnectException {
        this.context = messageContext;
        this.validateParameter();
        this.comprehendClient = createComprehendClient();
        execute(messageContext);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getParameter(String parameterName, Class<T> type) {
        return (T) getParameter(context, parameterName);
    }

    protected String getParameterAsString(String parameterName) {
        return getParameter(parameterName, String.class);
    }

    protected abstract void validateParameter();

    protected abstract void execute(final MessageContext messageContext) throws ConnectException;

    private AmazonComprehend createComprehendClient() {
        String awsAccessKeyId = getParameterAsString("awsAccessKeyId");
        String awsSecretAccessKey = getParameterAsString("awsSecretAccessKey");
        String region = getParameterAsString("region");

        Assert.assertNotNull("The awsAccessKeyId can't null", awsAccessKeyId);
        Assert.assertNotNull("The awsSecretAccessKey can't null", awsSecretAccessKey);
        Assert.assertNotNull("The region can't null", region);

        final BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

        return AmazonComprehendClientBuilder.standard().withCredentials(
            new AWSStaticCredentialsProvider(awsCreds)).withRegion(region).build();
    }

    public AmazonComprehend getComprehendClient() {
        return comprehendClient;
    }
    
}