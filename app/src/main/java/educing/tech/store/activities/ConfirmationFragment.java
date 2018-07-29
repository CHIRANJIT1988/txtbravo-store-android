package educing.tech.store.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import educing.tech.store.R;


public class ConfirmationFragment extends Fragment implements View.OnClickListener
{

    private static int SPLASH_TIME_OUT = 3000;

    public ConfirmationFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                try
                {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    // close this activity
                    getActivity().finish();
                }

                catch (Exception e)
                {

                }

            }

        }, SPLASH_TIME_OUT);

        return view;
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    @Override
    public void onClick(View view)
    {
        getActivity().finish();
    }
}
