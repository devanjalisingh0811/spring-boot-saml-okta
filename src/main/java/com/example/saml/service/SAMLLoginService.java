package com.example.saml.service;

import com.coveo.saml.SamlClient;
import com.coveo.saml.SamlException;
import com.coveo.saml.SamlResponse;
import com.example.saml.utils.XMLObjectUnpacker;
import com.example.saml.utils.XmlFormatter;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SAMLLoginService {


    private final static Logger LOGGER = LoggerFactory.getLogger(SAMLLoginService.class);

    public void doLogin(HttpServletResponse response) throws SamlException, IOException {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("metadata.xml");


        InputStreamReader inputStreamReader = new InputStreamReader(is);

        SamlClient samlClient = SamlClient.fromMetadata("devanjali",
                "http://localhost:8081/saml/sso",
                inputStreamReader);


        String encodedRequest = samlClient.getSamlRequest();
        String idpUrl = samlClient.getIdentityProviderUrl();
        LOGGER.info("IDP URL  :  " + idpUrl);
        LOGGER.info("Encoded Request  :  " + encodedRequest);
        // To initiate the authentication exchange
        samlClient.redirectToIdentityProvider(response, null);

    }


    // Pure Java
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());

    }

    public Map<String, Object> parseSAMLResponse(HttpServletRequest request) throws SamlException, IOException {

        Map<String, Object> stringStringMap = new HashMap<>();

        String encodedResponse = request.getParameter("SAMLResponse");
        LOGGER.info("User Authenticated :  " + encodedResponse);
        Response response = decodeAndValidateSamlResponse(encodedResponse);

        stringStringMap.put("issuer", response.getIssuer().getValue());
        stringStringMap.put("status", response.getStatus().getStatusCode().getValue());
        XMLObjectUnpacker xmlObjectUnpacker = new XMLObjectUnpacker();
        response.getIssuer().getValue();
        response.getAssertions().forEach(assertion -> {
            stringStringMap.put("nameId", assertion.getSubject().getNameID().getValue());

            assertion.getAttributeStatements().forEach(attributeStatement -> attributeStatement.getAttributes().forEach(attribute -> {
                String key = attribute.getName();
                List<String> strings = new ArrayList<>();
                attribute.getAttributeValues().forEach(xmlObject -> {
                    strings.add((xmlObjectUnpacker.getAttributeValue(xmlObject)));
                });

                stringStringMap.put(key, strings);
            }));
        });

        XmlFormatter xmlFormatter = new XmlFormatter();
        String data = xmlFormatter.format(new String(Base64.decode(encodedResponse), "UTF-8"));
        stringStringMap.put("xml", data);
        return stringStringMap;
    }


    public Response decodeAndValidateSamlResponse(String encodedResponse) throws SamlException {
        String decodedResponse;
        try {
            decodedResponse = new String(Base64.decode(encodedResponse), "UTF-8");
        } catch (UnsupportedEncodingException var6) {
            throw new SamlException("Cannot decode base64 encoded response", var6);
        }


        Response response;
        try {
            DOMParser parser = createDOMParser();
            parser.parse(new InputSource(new StringReader(decodedResponse)));
            response = (Response) Configuration.getUnmarshallerFactory().getUnmarshaller(parser.getDocument().getDocumentElement()).unmarshall(parser.getDocument().getDocumentElement());
        } catch (SAXException | UnmarshallingException | IOException var5) {
            throw new SamlException("Cannot decode xml encoded response", var5);
        }


        return response;
    }

    private static DOMParser createDOMParser() throws SamlException {
        DOMParser parser = new DOMParser() {
            {
                try {
                    this.setFeature("http://apache.org/xml/features/include-comments", false);
                } catch (Throwable var2) {
                    throw new SamlException("Cannot disable comments parsing to mitigate https://www.kb.cert.org/vuls/id/475445", var2);
                }
            }
        };
        return parser;
    }

}