package com.accio.userService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	JavaMailSender javaMailSender;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	public void sendEmail(String to, String subject, String body) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);

			javaMailSender.send(message);
			logger.info("Email sent to {}", to);
		} catch (Exception e) {
			logger.error("Failed to send email to {}: {}", to, e.getMessage());
			throw new RuntimeException("Failed to send email: " + e.getMessage());
		}
	}

}
