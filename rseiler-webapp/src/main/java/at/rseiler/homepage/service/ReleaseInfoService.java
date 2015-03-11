package at.rseiler.homepage.service;

/**
 * Provides build information about the webapp.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
public interface ReleaseInfoService {

    /**
     * The buildTime from the release.info
     *
     * @return the buildTime form the release.info
     */
    String getBuildTime();

    /**
     * The buildVersion from the release.info
     *
     * @return the buildVersion form the release.info
     */
    String getBuildVersion();

}
