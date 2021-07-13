package com.exadel.kaliada.core.services.impl;

import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationManager;
import com.adobe.granite.translation.api.TranslationService;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.exadel.kaliada.core.utils.ResourceResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=Harvard Translator Workflow Process",
                Constants.SERVICE_VENDOR + " = aem demo",
                Constants.SERVICE_DESCRIPTION + " = Custom harvard news translator step from en to ru"
        })
public class HarvardTranslatorWorkflowProcess implements WorkflowProcess {

    private static final String PAYLOAD_TYPE = "JCR_PATH";
    private static final String SOURCE_LOCALE = "/en/";
    private static final String TARGET_LOCALE = "/ru/";
    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "ru";
    private static final String PROPERTY_TO_TRANSLATE = "text";
    private static final String NODE_TO_TRANSLATE_PATH = "/jcr:content/root/container/text";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private TranslationManager translationManager;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            WorkflowData workflowData = workItem.getWorkflowData();
            if (workflowData.getPayloadType().equals(PAYLOAD_TYPE)) {
                ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resourceResolverFactory);
                String payload = workflowData.getPayload().toString() + NODE_TO_TRANSLATE_PATH;
                Resource resource = Optional.ofNullable(resourceResolver.getResource(payload)).orElseThrow();
                ModifiableValueMap valueMap = Optional.ofNullable(resource.adaptTo(ModifiableValueMap.class)).orElseThrow();
                String text = valueMap.get(PROPERTY_TO_TRANSLATE, "");
                String translatedText = translateText(resource, text);
                Resource differentLocaleResource = Optional.ofNullable(resourceResolver.getResource(payload.replace(SOURCE_LOCALE, TARGET_LOCALE))).orElseThrow();
                valueMap = Optional.ofNullable(differentLocaleResource.adaptTo(ModifiableValueMap.class)).orElseThrow();
                valueMap.put(PROPERTY_TO_TRANSLATE, translatedText);
                resourceResolver.commit();
            }
        } catch (PersistenceException | LoginException | TranslationException | NoSuchElementException e) {
            log.error("Can't execute Harvard Translator Workflow Process", e);
        }
    }

    private String translateText(Resource resource, String text) throws TranslationException {
        TranslationService translationService = translationManager.createTranslationService(resource);
        return translationService.translateString(text, SOURCE_LANGUAGE, TARGET_LANGUAGE, TranslationConstants.ContentType.HTML, null)
                .getTranslation();
    }
}
