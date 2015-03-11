package at.rseiler.homepage.service.impl;

import at.rseiler.homepage.service.ReleaseInfoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads the automatically generated release.info and extract the build time and build version from it.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
@Service
public class ReleaseInfoServiceImpl implements ReleaseInfoService {

    private Logger logger = Logger.getLogger(ReleaseInfoServiceImpl.class);

    /**
     * The release.info file.
     */
    private Resource releaseInfoResource;

    /**
     * The buildTime from the release.info file.
     */
    private String buildTime;

    /**
     * The buildVersion from the release.info file.
     */
    private String buildVersion;

    @Value("classpath:release.info")
    public void setReleaseInfoResource(Resource releaseInfoResource) {
        this.releaseInfoResource = releaseInfoResource;
    }

    @PostConstruct
    public void initialize() {
        Properties releaseProperties = new Properties();

        if (releaseInfoResource != null) {
            try {
                releaseProperties.load(releaseInfoResource.getInputStream());
            } catch (IOException e) {
                logger.error("Failed to load release.info", e);
            }
        }

        buildTime = releaseProperties.getProperty("build.time", "unknown");
        buildVersion = releaseProperties.getProperty("build.version", "unknown");
    }

    @Override
    public String getBuildTime() {
        return buildTime;
    }

    @Override
    public String getBuildVersion() {
        return buildVersion;
    }

}
