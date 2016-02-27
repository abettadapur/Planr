package devpost.yelp.planfun.net;

import android.app.Application;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import devpost.yelp.planfun.net.interfaces.AuthService;
import devpost.yelp.planfun.net.interfaces.CategoryService;
import devpost.yelp.planfun.net.interfaces.DirectionsService;
import devpost.yelp.planfun.net.interfaces.ItemService;
import devpost.yelp.planfun.net.interfaces.ItineraryService;
import devpost.yelp.planfun.net.serializers.CalendarSerializer;
import devpost.yelp.planfun.net.serializers.LatLngSerializer;
import okhttp3.Interceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Alex Andrey
 */
public class RestClient {
    private static final String BASE_URL="http://99.112.177.81:5000/api/";
    private static String TAG = "REST";
    private AuthService authService;
    private ItineraryService itineraryService;
    private ItemService itemService;
    private DirectionsService directionsService;
    private CategoryService categoryService;

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
        RestClient.context = context;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            Cache responseCache = new Cache(context.getCacheDir(), SIZE_OF_CACHE);
            builder.cache(responseCache);
        } catch (Exception e) {
            Log.d(TAG, "Unable to set http cache", e);
        }
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.connectTimeout(30, TimeUnit.SECONDS);



        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new CalendarSerializer())
                .registerTypeAdapter(GregorianCalendar.class, new CalendarSerializer())
                .registerTypeAdapter(LatLng.class, new LatLngSerializer())
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
                }).create();

        builder.networkInterceptors().add(mAuthCacheInterceptor);
        OkHttpClient client = builder.build();

        // Create Executor
        Executor executor = Executors.newCachedThreadPool();

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .build();

        authService = adapter.create(AuthService.class);
        itineraryService = adapter.create(ItineraryService.class);
        itemService = adapter.create(ItemService.class);
        directionsService = adapter.create(DirectionsService.class);
        categoryService = adapter.create(CategoryService.class);
    }

    public AuthService getAuthService()
    {
        return authService;
    }
    public ItineraryService getItineraryService() { return itineraryService; }
    public ItemService getItemService() { return itemService; }
    public DirectionsService getDirectionsService() { return directionsService; }
    public CategoryService getCategoryService(){ return categoryService; }

    private static final Interceptor mAuthCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            //See https://docs.google.com/presentation/d/1eJa0gBZLpZRQ5vjW-eqLyekEgB54n4fQ1N4jDcgMZ1E/edit#slide=id.g75a45c04a_079
            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder().addHeader("Authorization", AccessToken.getCurrentAccessToken().getToken());
            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                // 1000 s
                requestBuilder.addHeader("Cache-Control", "public, max-stale=1000");
            }
            Request headerRequest = requestBuilder.method(request.method(), request.body()).build();
            Log.d(TAG,"Request: "+headerRequest);
            Log.d(TAG,"Request headers: "+headerRequest.headers());
            return chain.proceed(headerRequest);
        }
    };
}
