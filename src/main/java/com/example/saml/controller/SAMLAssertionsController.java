package com.example.saml.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coveo.saml.SamlException;
import com.example.saml.service.SAMLLoginService;

@RestController
public class SAMLAssertionsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAMLAssertionsController.class);

	@Autowired
	SAMLLoginService samlLoginService;

	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("******login******");
		try {
			samlLoginService.doLogin(response);
		} catch (SamlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("******Logout******");

		try {
			samlLoginService.doLogout(response);
		} catch (SamlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
