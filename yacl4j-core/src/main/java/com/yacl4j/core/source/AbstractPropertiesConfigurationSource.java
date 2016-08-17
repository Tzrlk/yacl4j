package com.yacl4j.core.source;

import java.util.Enumeration;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yacl4j.core.ConfigurationSource;
import com.yacl4j.core.util.JacksonUtils;
import com.yacl4j.core.util.JsonPointerUtils;

abstract class AbstractPropertiesConfigurationSource implements ConfigurationSource {

	@Override
	public JsonNode getConfiguration() {
		Properties properties = getProperties();
		ObjectNode configuration = JacksonUtils.yamlObjectMapper().createObjectNode();
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyKey = (String) propertyNames.nextElement();
			JsonPointer propertyKeyAsJsonPointer = JsonPointerUtils.fromProperty(propertyKey);
			addJsonPointer(configuration, propertyKeyAsJsonPointer, properties.getProperty(propertyKey));
		}
		return configuration;
	}
	
	private void addJsonPointer(ObjectNode configuration, JsonPointer propertyKeyAsJsonPointer, String propertyValue) {
		ObjectNode currentNode = configuration;
		for (JsonPointer head : JsonPointerUtils.heads(propertyKeyAsJsonPointer)) {
			JsonNode headAsJsonNode = configuration.at(head);
			if (headAsJsonNode.isMissingNode() || headAsJsonNode.isValueNode()) {
				currentNode = currentNode.putObject(head.last().getMatchingProperty());
			} else {
				currentNode = (ObjectNode) headAsJsonNode;					
			}
		}
		if (!currentNode.has(propertyKeyAsJsonPointer.last().getMatchingProperty())) {
			JsonNode propertyValueAsJsonNode = JacksonUtils.yamlObjectMapper().readValueAsJsonNode(propertyValue);
			currentNode.set(propertyKeyAsJsonPointer.last().getMatchingProperty(), propertyValueAsJsonNode);
		}
	}
	
	protected abstract Properties getProperties();
	
}
