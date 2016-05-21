package fr.kissy.mapper.ghost;

import com.google.common.collect.Lists;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GhostMapper {
    private static final Logger logger = LoggerFactory.getLogger(GhostMapper.class);

    private GhostBeanMapper beanMapper;

    public GhostMapper() {
        beanMapper = new GhostBeanMapper();
    }

    public <T> T readValue(final WebDriver driver, final Class<T> objectType) {
        logger.debug("Reading value {}", objectType.getName());
        final List<String> rootCssSelectors = Lists.newArrayList();
        return beanMapper.readBean(driver, driver, objectType, rootCssSelectors);
    }
}