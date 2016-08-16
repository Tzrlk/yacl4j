package com.yacl4j.core.source;

import java.io.File;
import java.net.URL;

import com.yacl4j.core.ConfigurationBuilder;
import com.yacl4j.core.ConfigurationSource;
import com.yacl4j.core.util.FileUtils;

public class ConfigurationSourceBuilder {
	
	private final ConfigurationBuilder configurationBuilder;

	public ConfigurationSourceBuilder(ConfigurationBuilder configurationBuilder) {
		this.configurationBuilder = configurationBuilder;
	}

	public ConfigurationBuilder file(File file) {
		return this.configurationBuilder.source(selectFileConfigurationSource(file));
	}
	
	public ConfigurationBuilder fileFromClasspath(String filename) {
		URL configurationUrl = this.getClass().getClassLoader().getResource(filename);
		if (configurationUrl != null) {
			return this.configurationBuilder.source(selectFileConfigurationSource(FileUtils.toFile(configurationUrl)));
		} else {
			throw new IllegalStateException("Unable to find file on classpath: " + filename);
		}
	}
	
	public ConfigurationBuilder fileFromPath(String filename) {
		File configurationFile = new File(filename);
		if (configurationFile.exists()) {
			return this.configurationBuilder.source(selectFileConfigurationSource(configurationFile));
		} else {
			throw new IllegalStateException("Unable to find file on path: " + filename);
		}
	}
	
	public ConfigurationBuilder systemProperties() {
		return this.configurationBuilder.source(new SystemPropertiesConfigurationSource());
	}
	
	public ConfigurationBuilder environmentVariables() {
		return this.configurationBuilder.source(new EnvironmentVariablesConfigurationSource());
	}
	
	private ConfigurationSource selectFileConfigurationSource(File file) {
		if (file.getName().endsWith(".properties")) {
			return new PropertiesFileConfigurationSource(file);
		} else if (file.getName().endsWith(".yaml")) {
			return new YamlFileConfigurationSource(file);
		} else if (file.getName().endsWith(".json")) {
			return new JsonFileConfigurationSource(file);
		} else {
			throw new IllegalStateException("Configuration format not supported: " + file);
		}
	}
	
}