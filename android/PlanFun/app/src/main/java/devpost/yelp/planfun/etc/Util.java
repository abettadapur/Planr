package devpost.yelp.planfun.etc;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue;

import devpost.yelp.planfun.ui.views.CustomIcon;

/**
 * Created by alexb on 3/7/2016.
 */
public class Util
{
    public static IconValue iconFromString(String text) throws IllegalArgumentException {
        IconValue value = IconValue.valueOf(text.replace('-', '_').toUpperCase());
        return value;
    }

    public static CustomIcon customIconFromString(String text) throws IllegalArgumentException
    {
        CustomIcon value = CustomIcon.valueOf(text.replace('-', '_').toUpperCase());
        return value;
    }
}
