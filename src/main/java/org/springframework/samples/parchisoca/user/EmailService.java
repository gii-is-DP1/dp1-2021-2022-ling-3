package org.springframework.samples.parchisoca.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

@Component
public class EmailService
{
    @Autowired
    private JavaMailSender mailSender;

    private static final String INVITATION_SUBJECT = "Parchis and Oca Invitation";
    private static final String VERIFICATION_SUBJECT = "Complete your account registration";
    private static final String GREETING_TEXT = "Hello there, ";
    private static final String INVITATION_TEXT_1 = "You have been invited by user ";
    private static final String INVITATION_TEXT_2 = " to play a round of Parchis or Oca!";
    private static final String INVITATION_TEXT_END = "Come join us!";


    public void sendEmail(String email, String sender, String emailNow, String userSending)
    {
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(email);
        // message.setSubject(INVITATION_SUBJECT);
        // message.setText(GREETING_TEXT +"\n\n" + INVITATION_TEXT_1 + sender + INVITATION_TEXT_2 + "\n\n" + INVITATION_TEXT_END);
        // mailSender.send(message);


        //This doesnt work but i think it could be something similar
        String to = email;
        String from = emailNow;

      String host = "localhost";

      Properties properties = System.getProperties();

      properties.setProperty("mail.smtp.host", host);

      Session session = Session.getDefaultInstance(properties);

      try{

         MimeMessage message = new MimeMessage(session);


         message.setFrom(new InternetAddress(from));


         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));


         message.setSubject("This is the Subject Line!");


         message.setText("This is actual message");


         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }


    }
}
