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
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=Harvard Translator Workflow Process",
                Constants.SERVICE_VENDOR + " = aem demo",
                Constants.SERVICE_DESCRIPTION + " = Custom harvard news translator step from en to ru"
        })
@Slf4j
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
                Resource resource = resourceResolver.getResource(payload);
                ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
                String text = valueMap.get(PROPERTY_TO_TRANSLATE, StringUtils.EMPTY);
                String translatedText = translateText(resource, text);
                Resource differentLocaleResource = resourceResolver.getResource(payload.replace(SOURCE_LOCALE, TARGET_LOCALE));
                valueMap = differentLocaleResource.adaptTo(ModifiableValueMap.class);
                valueMap.put(PROPERTY_TO_TRANSLATE, translatedText);
                resourceResolver.commit();
            }
        } catch (PersistenceException | LoginException e) {
            log.error("Can't execute Harvard Translator Workflow Process, please check that user has sufficient rights", e);
        } catch (TranslationException e) {
            log.error("Can't execute Harvard Translator Workflow Process, please check translation engine", e);
        } catch (NullPointerException e) {
            log.error("Can't execute Harvard Translator Workflow Process, some resource not available", e);
        }
    }

    private String translateText(Resource resource, String text) throws TranslationException {
        TranslationService translationService = translationManager.createTranslationService(resource);
        return translationService.translateString(text, SOURCE_LANGUAGE, TARGET_LANGUAGE, TranslationConstants.ContentType.HTML, null)
                .getTranslation();
    }
}
