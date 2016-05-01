package com.ec3;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AWSCredentials credentials = null;
        try {
            credentials = new ClasspathPropertiesFileCredentialsProvider().getCredentials();
        } catch (Exception e) {
        	response.getWriter().println("Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your AwsCredentials.properties file is at the correct " +
                    "location (src folder), and is in valid format: " + e.getMessage());
        	return;
        }
        String message = request.getParameter("message");
        if(message == null){
        		response.getWriter().println("Please pass a parameter 'message'<br/>");
        		response.getWriter().println("Enter http://<server>/<appname>/send?message=<your message><br/>");
        		return;
        }
        try {
            AmazonSQS sqs = new AmazonSQSClient(credentials);
            String myQueueUrl = this.getInitParameter("SQSDetailsEndPoint");
            sqs.setEndpoint(myQueueUrl);
            SendMessageResult result = sqs.sendMessage(new SendMessageRequest(myQueueUrl, message));
            response.getWriter().println("Message with id: " + result.getMessageId() + " sent successfully");
        }
        catch (Exception ex) {
        		response.getWriter().println("Error sending message to the Queue <br/>");
        		response.getWriter().println("Reason: " + ex.getMessage());
        } 
	}
}
