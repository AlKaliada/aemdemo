package com.exadel.kaliada.core.schedulers;

import com.exadel.kaliada.core.services.HarvardNewsParser;
import com.exadel.kaliada.core.services.HarvardSingleNewsPageCreator;
import com.exadel.kaliada.core.utils.HarvardNewsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Map;

/**
 * Class to load news from HarvardNewsParser service, and then create news pages by HarvardSingleNewsPageCreator service
 * @author akaliada
 */

@Slf4j
@Designate(ocd = HarvardNewsScheduler.Config.class)
@Component(immediate = true, service = Runnable.class)
public class HarvardNewsScheduler implements Runnable{

    @ObjectClassDefinition(name = "harvard news scheduler")
    public static @interface Config{

        @AttributeDefinition(name = "Cron expression")
        String cronExpression() default "0 0/1 0 ? * * *";

        @AttributeDefinition(name = "Scheduler name")
        String schedulerName() default "Custom Sling Scheduler Configuration";
    }

    @Reference
    private Scheduler scheduler;

    @Reference
    private HarvardSingleNewsPageCreator harvardSingleNewsPageCreator;

    @Reference
    private HarvardNewsParser harvardNewsParser;

    @Activate
    private void activate(Config config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
        scheduleOptions.name(config.schedulerName());
        scheduleOptions.canRunConcurrently(true);
        scheduler.schedule(this, scheduleOptions);
        log.info("harvard news scheduler activated");
    }

    @Deactivate
    private void deactivate(Config config) {
        scheduler.unschedule(config.schedulerName());
        log.info("harvard news scheduler unactivated");
    }

    @Override
    public void run() {
        HarvardNewsUtil.updateNews(harvardNewsParser, harvardSingleNewsPageCreator);
    }
}
