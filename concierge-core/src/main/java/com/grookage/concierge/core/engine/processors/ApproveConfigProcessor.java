package com.grookage.concierge.core.engine.processors;

import com.grookage.concierge.core.engine.ConciergeContext;
import com.grookage.concierge.core.engine.ConciergeProcessor;
import com.grookage.concierge.core.exception.ConciergeErrorCode;
import com.grookage.concierge.core.exception.ConciergeException;
import com.grookage.concierge.models.config.ConfigDetails;
import com.grookage.concierge.models.config.ConfigEvent;
import com.grookage.concierge.models.config.ConfigKey;
import com.grookage.concierge.models.config.ConfigState;
import com.grookage.concierge.repository.ConciergeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@AllArgsConstructor
@Slf4j
@Getter
public class ApproveConfigProcessor extends ConciergeProcessor {

    private final Supplier<ConciergeRepository> repositorySupplier;

    @Override
    public ConfigEvent name() {
        return ConfigEvent.APPROVE_CONFIG;
    }

    @Override
    @SneakyThrows
    public void process(ConciergeContext context) {
        final var configKey = context.getContext(ConfigKey.class)
                .orElseThrow((Supplier<Throwable>) () -> ConciergeException.error(ConciergeErrorCode.VALUE_NOT_FOUND));
        final var storedConfig = getRepositorySupplier().get().getRecord(configKey).orElse(null);
        if (null == storedConfig || storedConfig.getConfigState() != ConfigState.CREATED) {
            log.error("There are no stored configs present with namespace {}, version {} and configName {}. Please try updating them instead",
                    configKey.getNamespace(),
                    configKey.getVersion(),
                    configKey.getConfigName());
            throw ConciergeException.error(ConciergeErrorCode.NO_CONFIG_FOUND);
        }
        addHistory(context, storedConfig);
        getRepositorySupplier().get().update(storedConfig);
        context.addContext(ConfigDetails.class.getSimpleName(), storedConfig);
    }
}
