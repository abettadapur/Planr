package devpost.yelp.planfun.net.serializers;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author Alex
 */
public class LatLngSerializer implements JsonSerializer<LatLng>, JsonDeserializer<LatLng>
{
    @Override
    public LatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject coordinate = (JsonObject)json;

        return new LatLng(coordinate.getAsJsonPrimitive("latitude").getAsDouble(), coordinate.getAsJsonPrimitive("longitude").getAsDouble());
    }

    @Override
    public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.latitude+","+src.longitude);
    }
}