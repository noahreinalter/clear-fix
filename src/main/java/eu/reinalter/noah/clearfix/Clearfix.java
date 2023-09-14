package eu.reinalter.noah.clearfix;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clearfix implements ModInitializer {
    private static Logger LOGGER;

    @Override
    public void onInitialize() {
        LOGGER = LoggerFactory.getLogger("Clear-fix");

        LOGGER.info("Started Clear-fix mod");
    }

    public static Logger logger() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger not yet available");
        }

        return LOGGER;
    }
}