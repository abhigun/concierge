package com.grookage.concierge.core.utils;

import com.grookage.concierge.core.managers.VersionGenerator;
import com.grookage.concierge.models.MapperUtils;
import com.grookage.concierge.models.config.ConfigDetails;
import com.grookage.concierge.models.config.ConfigKey;
import com.grookage.concierge.models.config.ConfigState;
import com.grookage.concierge.models.ingestion.ConfigurationRequest;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationUtils {

    @SneakyThrows
    public ConfigDetails toCreateConfigRequest(ConfigurationRequest configurationRequest,
                                               VersionGenerator versionGenerator) {
        return ConfigDetails.builder()
                .configKey(ConfigKey.builder()
                        .namespace(configurationRequest.getNamespace())
                        .version(versionGenerator.getVersionId("V"))
                        .configName(configurationRequest.getConfigName())
                        .configType(configurationRequest.getConfigType())
                        .build())
                .configState(ConfigState.CREATED)
                .description(configurationRequest.getDescription())
                .data(MapperUtils.mapper().writeValueAsBytes(configurationRequest.getData()))
                .build();
    }

}
