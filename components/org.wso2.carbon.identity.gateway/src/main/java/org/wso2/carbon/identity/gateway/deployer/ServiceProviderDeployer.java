package org.wso2.carbon.identity.gateway.deployer;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.deployment.engine.Artifact;
import org.wso2.carbon.deployment.engine.ArtifactType;
import org.wso2.carbon.deployment.engine.Deployer;
import org.wso2.carbon.deployment.engine.exception.CarbonDeploymentException;
import org.wso2.carbon.identity.gateway.common.model.sp.ServiceProviderConfig;
import org.wso2.carbon.identity.gateway.internal.FrameworkServiceComponent;
import org.wso2.carbon.identity.gateway.store.ServiceProviderConfigStore;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServiceProviderDeployer implements Deployer {

    private ArtifactType artifactType;
    private URL repository;


    private Logger logger = LoggerFactory.getLogger(FrameworkServiceComponent.class);

    @Override
    public void init() {
        artifactType = new ArtifactType<>("sp");


        try {
            repository = new URL("file:" + Paths.get(System.getProperty("carbon.home", "."), "deployment", "providers", "sp")
                                         .toString
                    ());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String deploy(Artifact artifact) throws CarbonDeploymentException {
        ServiceProviderConfig serviceProviderConfig = getServiceProviderConfig(artifact);
        ServiceProviderConfigStore.getInstance().addServiceProvider(serviceProviderConfig);
        return artifact.getName();
    }

    @Override
    public void undeploy(Object key) throws CarbonDeploymentException {
        if (!(key instanceof String)) {
            throw new CarbonDeploymentException("Error while Un Deploying : " + key + "is not a String value");
        }
        logger.debug("Undeploying : " + key);

    }

    @Override
    public Object update(Artifact artifact) throws CarbonDeploymentException {
        logger.debug("Updating : " + artifact.getName());

        return artifact.getName();
    }

    @Override
    public URL getLocation() {

        logger.debug("Updating : "  );

        return repository;
    }

    @Override
    public ArtifactType getArtifactType() {

        logger.debug("Updating : "  );
        return artifactType;
    }

    /**
     * Read the artifacts and save the policy and metadata to PolicyStore and PolicyCollection
     * @param artifact deployed articles
     */
    private synchronized ServiceProviderConfig getServiceProviderConfig(Artifact artifact) {
        String artifactName = artifact.getName();
        ServiceProviderConfig serviceProviderConfig = null ;
        Path path = Paths.get(artifactName);
        if (Files.exists(path)) {
            try {
                Reader in = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8);
                Yaml yaml = new Yaml();
                yaml.setBeanAccess(BeanAccess.FIELD);
                serviceProviderConfig = yaml.loadAs(in, ServiceProviderConfig.class);


            } catch (Exception e) {

            }
        }
        return serviceProviderConfig ;
    }
}
