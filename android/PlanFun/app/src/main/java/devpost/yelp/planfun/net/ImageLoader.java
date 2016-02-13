package devpost.yelp.planfun.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alex on 3/24/2015.
 */
public class ImageLoader extends AsyncTask<String, Void, Bitmap>
{
    private ImageView view;

    public ImageLoader(ImageView view)
    {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap b = null;
        try {
            URL url = new URL(params[0]);
            Log.e("IMAGE_URL", params[0]);
            b = BitmapFactory.decodeStream(url.openStream());
        }
        catch(MalformedURLException malurlex)
        {
            Log.e("IMAGE_URL", malurlex.getMessage());
        }
        catch(IOException ioex)
        {
            Log.e("IMAGE_URL", ioex.getMessage());
        }
        return b;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}
