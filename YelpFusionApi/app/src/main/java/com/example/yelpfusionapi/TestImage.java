package com.example.yelpfusionapi;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;



public class TestImage extends Fragment {
    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;
    private ImageView img_4;

    private OnFragmentInteractionListener mListener;

    public TestImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_test_image, container, false);

        img_1 = (ImageView) view.findViewById(R.id.imageView2);
        img_2 = (ImageView) view.findViewById(R.id.imageView3);
        img_3 = (ImageView) view.findViewById(R.id.imageView4);
        img_4 = (ImageView) view.findViewById(R.id.imageView5);

        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("!!!!!!!!!!!!!!!!!!!MAP");
                img_1.setImageResource(R.drawable.map_white);
                img_2.setImageResource(R.drawable.message);
                img_3.setImageResource(R.drawable.event);
                img_4.setImageResource(R.drawable.me);
            }
        });

        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("!!!!!!!!!!!!!!!!!!!MSG");
                img_1.setImageResource(R.drawable.map);
                img_2.setImageResource(R.drawable.message_white);
                img_3.setImageResource(R.drawable.event);
                img_4.setImageResource(R.drawable.me);
            }
        });

        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("!!!!!!!!!!!!!!!!!!!EVENT");
                img_1.setImageResource(R.drawable.map);
                img_2.setImageResource(R.drawable.message);
                img_3.setImageResource(R.drawable.event_white);
                img_4.setImageResource(R.drawable.me);
            }
        });

        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("!!!!!!!!!!!!!!!!!!!ME");
                img_1.setImageResource(R.drawable.map);
                img_2.setImageResource(R.drawable.message);
                img_3.setImageResource(R.drawable.event);
                img_4.setImageResource(R.drawable.me_white);
            }
        });

        return view;
    }

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
