package cs591e1_sp19.eatogether;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


public class RestaurantInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    private TextView Rest_Name;
    private RatingBar ratingBar;
    private TextView dollar;
    private  TextView info;

    private OnFragmentInteractionListener mListener;

    public RestaurantInfo() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);  //separate me from return statement.
        Rest_Name = (TextView)view.findViewById(R.id.Rest_Name);
        dollar = (TextView) view.findViewById(R.id.dollar);
        ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
        info = (TextView)view.findViewById(R.id.info);

        Bundle bundle = getArguments();

        Rest_Name.setText(bundle.getString("rest_name"));
        dollar.setText(bundle.getString("rest_price"));
        info.setText(bundle.getString("rest_info"));
        ratingBar.setRating(bundle.getFloat("rest_rating"));

        getArguments().putBoolean("ifClick", false);



        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
