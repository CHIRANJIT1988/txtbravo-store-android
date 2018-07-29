package educing.tech.store;

import android.content.Context;
import android.content.Intent;

import static educing.tech.store.configuration.Configuration.API_URL;


public final class CommonUtilities
{
	
	// give your server registration url here
    public static final String SERVER_URL = API_URL;

    // Google project id
    public static final String SENDER_ID = "286545281398";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Store Application";

    public static final String DISPLAY_MESSAGE_ACTION =
            "educing.tech.store.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */


    public static void displayMessage(Context context, String message)
    {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}