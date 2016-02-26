package devpost.yelp.planfun.net.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Alex
 */
public class CalendarSerializer implements JsonSerializer<Calendar>, JsonDeserializer<Calendar>
{

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_str = json.getAsString();
        Calendar date = null;
        try {
            sdf.parse(date_str);
            date = sdf.getCalendar();
        }
        catch(ParseException pex)
        {
            throw new IllegalStateException("Parse Error: "+date_str);
        }
        return date;
    }

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return new JsonPrimitive(sdf.format(src.getTime()));
    }
}