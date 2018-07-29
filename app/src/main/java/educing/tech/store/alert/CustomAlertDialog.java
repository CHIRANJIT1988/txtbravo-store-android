package educing.tech.store.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import educing.tech.store.helper.OnTaskCompleted;


public class CustomAlertDialog 
{

	private Context context;
	private OnTaskCompleted listener;
	
	
	public CustomAlertDialog(Context _context, OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = _context;
	}


	public void showConfirmationDialog(String message, final String action)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(message).setTitle("Confirmation").setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("YES", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						listener.onTaskCompleted(true, 200, action);
					}
				})

						// No button click action
				.setNegativeButton("NO THANKS", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});


		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}


	public void showUpdateConfirmationDialog(String title, String message, final String action)
	{

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(message).setTitle(title).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("UPDATE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						listener.onTaskCompleted(true, 200, action);
					}
				})


						// No button click action
				.setNegativeButton("NO THANKS", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// if this button is clicked, just close
						// the dialog box and do nothing
						// listener.onTaskCompleted(true, 199, action);
						dialog.cancel();
					}
				});


		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}


	public void showOKDialog(String title, String message, final String action)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//listener.onTaskCompleted(true, 200, action);
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}
}