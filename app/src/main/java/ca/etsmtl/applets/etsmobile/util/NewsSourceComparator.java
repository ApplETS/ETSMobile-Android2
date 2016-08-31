package ca.etsmtl.applets.etsmobile.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.Normalizer;
import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.NewsSource;
import ca.etsmtl.applets.etsmobile.model.Seances;

/**
 * Created by gnut3ll4 on 10/09/15.
 */
public class NewsSourceComparator implements Comparator<NewsSource> {
    @Override
    public int compare(NewsSource newsSource1, NewsSource newsSource2) {

        String sourceName1 = StringUtils.stripAccents(newsSource1.getName());
        String sourceName2 = StringUtils.stripAccents(newsSource2.getName());

        return sourceName1.toLowerCase().compareTo(sourceName2.toLowerCase());
    }
}
