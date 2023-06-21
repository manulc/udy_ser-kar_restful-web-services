package com.mlorenzo.app.ws.shared;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.mlorenzo.app.ws.shared.dtos.UserDto;

@Component
public class EmailSender {
	private final String FROM = "jhon.doe@photoapp.com";
	private final String VERIFY_EMAIL_SUBJECT = "One last step to complete your registration with PhotoApp";
	private final String PASSWORD_RESET_REQUEST_SUBJECT = "Password reset request";
	private final String VERIFY_EMAIL_HTMLCONTENT = "<h1>Please verify your email address</h1>"
			+ "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " click on the following link: "
			+ "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>"
			+ "Final step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside!";
	private final String PASSWORD_RESET_REQUEST_HTMLCONTENT = "<h1>A request to reset your password</h1>"
		      + "<p>Hi, $firstName!</p> "
		      + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
		      + " otherwise please click on the link below to set a new password: " 
		      + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
		      + " Click this link to Reset Password"
		      + "</a><br/><br/>"
		      + "Thank you!";
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void sendVerifyEmail(UserDto userDto) {
		String htmlContentWithToken = VERIFY_EMAIL_HTMLCONTENT.replace("$tokenValue", userDto.getEmailVerificationToken());
        sendEmail(FROM, userDto.getEmail(), VERIFY_EMAIL_SUBJECT, htmlContentWithToken);
    }
	
	public void sendPasswordResetRequestEmail(String firstName, String email, String token) {
		String htmlContentWithToken = PASSWORD_RESET_REQUEST_HTMLCONTENT.replace("$tokenValue", token).replace("$firstName", firstName);
        sendEmail(FROM, email, PASSWORD_RESET_REQUEST_SUBJECT, htmlContentWithToken);
    }
	
	private void sendEmail(String from, String to, String subject, String htmlContent) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setContent(htmlContent, MediaType.TEXT_HTML_VALUE);
            javaMailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Error sending mail", e);
        }
        System.out.println("Email sent!");
	}
}
