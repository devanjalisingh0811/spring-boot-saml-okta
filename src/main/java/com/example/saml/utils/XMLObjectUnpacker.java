package com.example.saml.utils;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;

public class XMLObjectUnpacker {
	public String getAttributeValue(XMLObject attributeValue) {
		return attributeValue == null ? null
				: attributeValue instanceof XSString ? getStringAttributeValue((XSString) attributeValue)
						: attributeValue instanceof XSAnyImpl ? getAnyAttributeValue((XSAnyImpl) attributeValue)
								: attributeValue.toString();
	}

	private String getStringAttributeValue(XSString attributeValue) {
		return attributeValue.getValue();
	}

	private String getAnyAttributeValue(XSAnyImpl attributeValue) {
		return attributeValue.getTextContent();
	}
}
