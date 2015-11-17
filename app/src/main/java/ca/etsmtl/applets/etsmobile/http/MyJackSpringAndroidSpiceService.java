package ca.etsmtl.applets.etsmobile.http;

import android.app.Application;


import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;
import com.octo.android.robospice.persistence.springandroid.json.jackson2.Jackson2ObjectPersisterFactory;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by laurencedevillers on 2014-10-11.
 */
public class MyJackSpringAndroidSpiceService extends JacksonSpringAndroidSpiceService {

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new Jackson2ObjectPersisterFactory(application));
        return cacheManager;
    }

    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Set Converters
        final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate.getMessageConverters();

        listHttpMessageConverters.add(jsonConverter);
        restTemplate.setMessageConverters(listHttpMessageConverters);
        return restTemplate;
    }
}
