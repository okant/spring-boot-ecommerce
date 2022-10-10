package com.luv2code.ecommerce.config;

import com.luv2code.ecommerce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import java.util.ArrayList;

// Bu class'ı yazma amacımız, Spring Data Rest ile otomatik olarak generate edilen PUT POST ve DELETE metodlarını disable etmek
// Configuration annotationı ile Spring otomatik olarak algılayıp burayı da çalıştıracak

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] allowedOrigins;
    private final EntityManager entityManager;

    @Autowired
    public MyDataRestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry registry) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, registry);

        HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH};

        disableHttpMethods(Product.class, config, theUnsupportedActions);

        disableHttpMethods(ProductCategory.class, config, theUnsupportedActions);

        disableHttpMethods(Country.class, config, theUnsupportedActions);

        disableHttpMethods(State.class, config, theUnsupportedActions);

        disableHttpMethods(Order.class, config, theUnsupportedActions);

        //registry.addMapping("/api/**").allowedOrigins(allowedOrigins);
        registry.addMapping(config.getBasePath() + "/**").allowedOrigins(allowedOrigins);

        exposeIds(config);
    }

    private static void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration().forDomainType(theClass).withItemExposure(((md, httpMethods) -> httpMethods.disable(theUnsupportedActions)))
                .withCollectionExposure(((md, httpMethods) -> httpMethods.disable(theUnsupportedActions)));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        // Get a list of all entity classes from the entity manager.
        // Bu sayede @Entity olarak eklediğimiz classlardaki id alanını alabiliyoruz

        var entities = entityManager.getMetamodel().getEntities();

        var entityClasses = new ArrayList<Class>();

        for (var entityType: entities) {
            entityClasses.add(entityType.getJavaType());
        }

        var domainTypes = entityClasses.toArray(new Class[0]);

        config.exposeIdsFor(domainTypes);
    }
}
