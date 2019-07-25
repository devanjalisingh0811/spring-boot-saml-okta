package com.example.saml.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.coveo.saml.SamlException;
import com.example.saml.service.SAMLLoginService;

@Controller
public class ConsumerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);

	@Autowired
	SAMLLoginService samlLoginService;

	@RequestMapping("/saml/sso")
	public ModelAndView catchAssertions(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SamlException {
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		Map<String, Object> stringStringMap = samlLoginService.parseSAMLResponse(request);
		ModelAndView modelAndView = new ModelAndView("test");
		modelAndView.addAllObjects(stringStringMap);

		return modelAndView;
	}
	
	@RequestMapping("/saml/sso1")
	public ModelAndView catchAssertions1(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SamlException {
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++");

		ModelAndView modelAndView = new ModelAndView("test");

		return modelAndView;
	}
	
	
	
	

	@RequestMapping("/saml/sso/logout")
	public ModelAndView singleLogout(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SamlException {
		LOGGER.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&log out");
		ModelAndView modelAndView = new ModelAndView("test");

		return modelAndView;
	}
}
