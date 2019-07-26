package com.example.saml.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.coveo.saml.SamlClient;
import com.coveo.saml.SamlClient.SamlIdpBinding;
import com.coveo.saml.SamlException;
import com.coveo.saml.SamlResponse;
import com.example.saml.utils.XMLObjectUnpacker;
import com.example.saml.utils.XmlFormatter;

@Service
public class SAMLLoginService {

	private final static Logger LOGGER = LoggerFactory.getLogger(SAMLLoginService.class);

	SamlClient samlClient = null;

	public void doLogin(HttpServletResponse response) throws SamlException, IOException {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("metadata.xml");

		InputStreamReader inputStreamReader = new InputStreamReader(is);

		samlClient = SamlClient.fromMetadata("deva_saml_poc", "https://localhost:8081/saml/sso", inputStreamReader,
				SamlIdpBinding.POST);
		/*
		 * SamlClient samlClient = SamlClient.fromMetadata("deva_saml_poc",
		 * "http://localhost:8081/saml/sso", inputStreamReader,
		 * SamlIdpBinding.Redirect);
		 */

		String encodedRequest = samlClient.getSamlRequest();
		String idpUrl = samlClient.getIdentityProviderUrl();
		LOGGER.info("IDP URL  :  " + idpUrl);
		LOGGER.info("Encoded Request  :  " + encodedRequest);
		// To initiate the authentication exchange
		samlClient.redirectToIdentityProvider(response, null);

	}

	public void doLogout(HttpServletResponse response) throws SamlException, IOException {

	}

	public Map<String, Object> parseSAMLResponse(HttpServletRequest request) throws SamlException, IOException {

		Map<String, Object> stringStringMap = new HashMap<>();

		String encodedResponse = request.getParameter("SAMLResponse");
		LOGGER.info("User Authenticated :  " + encodedResponse);
		SamlResponse response = samlClient.decodeAndValidateSamlResponse(encodedResponse);

		stringStringMap.put("issuer", response.getAssertion().getIssuer().getValue());
		stringStringMap.put("issueInstant", response.getAssertion().getIssueInstant());
		stringStringMap.put("nameId", response.getNameID());

		XMLObjectUnpacker xmlObjectUnpacker = new XMLObjectUnpacker();

		Assertion assertion = response.getAssertion();

		assertion.getAttributeStatements()
				.forEach(attributeStatement -> attributeStatement.getAttributes().forEach(attribute -> {
					String key = attribute.getName();
					List<String> strings = new ArrayList<>();
					attribute.getAttributeValues().forEach(xmlObject -> {
						strings.add((xmlObjectUnpacker.getAttributeValue(xmlObject)));
					});

					stringStringMap.put(key, strings);
				}));

		XmlFormatter xmlFormatter = new XmlFormatter();
		String data = xmlFormatter.format(new String(Base64.decodeBase64(encodedResponse), "UTF-8"));

		stringStringMap.put("xml", data);
		return stringStringMap;
	}

}
