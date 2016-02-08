package thingswithworth.org.funtimes.net;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import thingswithworth.org.funtimes.net.serializers.CalendarSerializer;
import thingswithworth.org.funtimes.net.serializers.LatLngSerializer;
import thingswithworth.org.funtimes.net.services.AuthService;

/**
 * @author Alex Andrey
 */
public class RestClient {
    private static final String BASE_URL="http://bettadapur.com:3000";
    private String TAG = "REST";
    private AuthService authService;

    private static RestClient client;
    private static Application context;
    private long SIZE_OF_CACHE = 1024 * 1024 * 10;//10 Meg, not so much

    public static RestClient init(Application context)
    {
        if(client==null)
        {
            client = new RestClient(context);
        }
        return client;
    }

    public static RestClient getInstance()
    {
        if(client==null)
        {
            throw new IllegalStateException("Trying to get TransitTimesRESTServices without init being called.");
        }
        return client;
    }

    private RestClient(Application context)
    {
        this.context = context;
        OkHttpClient caching_client = new OkHttpClient();
        try {
            Cache responseCache = new Cache(context.getCacheDir(), SIZE_OF_CACHE);
            caching_client.setCache(responseCache);
        } catch (Exception e) {
            Log.d(TAG, "Unable to set http cache", e);
        }
        caching_client.setReadTimeout(120, TimeUnit.SECONDS);
        caching_client.setConnectTimeout(120, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new CalendarSerializer())
                .registerTypeAdapter(GregorianCalendar.class, new CalendarSerializer())
                .registerTypeAdapter(LatLng.class, new LatLngSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mmZ")
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.deserialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .create();

        caching_client.networkInterceptors().add(mCacheControlInterceptor);

        // Create Executor
        Executor executor = Executors.newCachedThreadPool();

        RestAdapter adapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(executor, executor)
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(caching_client))
                .setConverter(new GsonConverter(gson))
                .build();

        authService = adapter.create(AuthService.class);
    }

    private static final Interceptor mCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //See https://docs.google.com/presentation/d/1eJa0gBZLpZRQ5vjW-eqLyekEgB54n4fQ1N4jDcgMZ1E/edit#slide=id.g75a45c04a_079
            Request request = chain.request();
            if(request.httpUrl().toString().contains("realtime"))
                return chain.proceed(request).newBuilder().build();

            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                // 4 weeks stale
                request.newBuilder()
                        .header("Cache-Control", "public, max-stale=2419200")
                        .build();
            }

            Response response = chain.proceed(request);

            // Re-write response CC header to force use of cache
            //This should be on server side really
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=86400") // 1 day
                    .build();
        }
    };
}
