package devpost.yelp.planfun.model;

import java.util.Map;

/**
 * Created by Alex on 3/30/2015.
 */
public class Directions
{
    private Map<String, Object> overview_polyline;
    private String summary;

    public Directions() {
    }

    public Map<String, Object> getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(Map<String, Object> overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
