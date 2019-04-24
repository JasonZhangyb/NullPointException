package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuFragment extends Fragment {

    private ImageView Map;
    private ImageView Message;
    private ImageView Event;
    private ImageView Me;
    private TextView txMap;
    private TextView txMessage;
    private TextView txEvent;
    private TextView txMe;
    private int count=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fragment,container,false);

        Map = (ImageView) v.findViewById(R.id.imgMap);
        Message = (ImageView) v.findViewById(R.id.imgMessage);
        Event = (ImageView) v.findViewById(R.id.imgEvent);
        Me = (ImageView) v.findViewById(R.id.imgMe);
        txMap = (TextView) v.findViewById(R.id.tvMap);
        txMessage = (TextView) v.findViewById(R.id.tvMessage);
        txEvent = (TextView) v.findViewById(R.id.tvEvent);
        txMe = (TextView) v.findViewById(R.id.tvMe);

        Map.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Map!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                Map.setImageResource(R.drawable.icon_01_white);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        Message.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               // Toast.makeText(getActivity(),"Message!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ListOfChatsActivity.class);
                startActivity(intent);
                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02_white);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        Event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Event!",Toast.LENGTH_SHORT).show();


                FirebaseDatabase.getInstance().getReference().child("Users").child(AppState.userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if (snapshot.getKey().equals("Ongoing")){
                                        Intent intent = new Intent(getActivity(), OnGoingActivity.class);
                                        startActivity(intent);
                                        count = 1;

                                    }

                                }
                                if (count==0){

                                    Intent intent = new Intent(getActivity(), NoOngingEventNotice.class);
                                    startActivity(intent);
                                    System.out.println("hihhhhhh");


                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03_white);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        Me.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Me!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04_white);
            }

        });

        txMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Map!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                Map.setImageResource(R.drawable.icon_01_white);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        txMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Message!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ListOfChatsActivity.class);
                startActivity(intent);
                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02_white);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        txEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getActivity(),"Event!",Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Users").child(AppState.userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if (snapshot.getKey().equals("Ongoing")){
                                        Intent intent = new Intent(getActivity(), OnGoingActivity.class);
                                        startActivity(intent);
                                        count = 1;

                                    }

                                }
                                if (count==0){

                                    Intent intent = new Intent(getActivity(), NoOngingEventNotice.class);
                                    startActivity(intent);
                                    System.out.println("hihhhhhh");


                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03_white);
                Me.setImageResource(R.drawable.icon_04);
            }

        });

        txMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(),"Me!",Toast.LENGTH_SHORT).show();
                Map.setImageResource(R.drawable.icon_01);
                Message.setImageResource(R.drawable.icon_02);
                Event.setImageResource(R.drawable.icon_03);
                Me.setImageResource(R.drawable.icon_04_white);
            }

        });


        return v;
    }
}
