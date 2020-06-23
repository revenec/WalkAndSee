package mvf.mikevidev.walkandsee;

import android.content.Context;
import android.widget.Toast;

public class Utilities
{
    public static final String key = "AIzaSyBdqGweqcB97eit2khrIBE6yagMjSb8dIg";
    public static final String basicColorApp = "#7A63FF";
    public static final String secondColorApp = "#74E0EF";
    public static final String greyColorApp = "A8A8A8";

    public static boolean isBlank(String text)
    {
        if(text == null)
        {
            return true;
        }
        else if("".equals(text) || "".equals(text.trim()))
        {
            return true;
        }
        return false;
    }

    public static void toastMessage(String text, Context context)
    {
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
