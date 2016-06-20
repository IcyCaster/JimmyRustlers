package com.project.uoa.carpooling;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.project.uoa.carpooling.adapters.EventToCardAdapter;
import com.project.uoa.carpooling.entities.CarPoolEventEntity;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;
import com.project.uoa.carpooling.jsonparsers.Facebook_Id_Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarPools.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarPools#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarPools extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private ArrayList<CarPoolEventEntity> listCarPoolEvents = new ArrayList<CarPoolEventEntity>();

private AccessToken accessToken = AccessToken.getCurrentAccessToken();

    private ArrayList<String> listOfEvents;

    private OnFragmentInteractionListener mListener;

    private RecyclerView rv;

    public CarPools() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarPools.
     */
    // TODO: Rename and change types and number of parameters
    public static CarPools newInstance(String param1, String param2) {
        CarPools fragment = new CarPools();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



//        List<CarPoolEventEntity> data = fill_with_data();


        view = inflater.inflate(R.layout.fragment_car_pools, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        GetEventIds();
        List<CarPoolEventEntity> carPoolEventEntities = GetEventDetails();
        EventToCardAdapter adapter = new EventToCardAdapter(carPoolEventEntities, getActivity());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));





//        testBatch();


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // This will eventually be a firebase call
    void GetEventIds() {

        long unixTime = System.currentTimeMillis() / 1000L;

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/events",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        //This parses the events the users has "subscribed" to. (Currently it just parses upcoming facebook events
                        listOfEvents = Facebook_Id_Response.parse(response.getJSONObject());

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        parameters.putString("since", Long.toString(unixTime));
        request.setParameters(parameters);
        request.executeAsync();

    }

    List<CarPoolEventEntity> GetEventDetails() {

        final ArrayList<CarPoolEventEntity> carPoolEventEntities = new ArrayList<CarPoolEventEntity>();

        for (String s : listOfEvents) {

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/" + s,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {

                            carPoolEventEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));


                        }
                    });

            request.executeAsync();

        }


        return carPoolEventEntities;
    }


    public List<CarPoolEventEntity> fill_with_data() {

        List<CarPoolEventEntity> data = new ArrayList<>();

        data.add(new CarPoolEventEntity(1, R.drawable.test_bbq, "TEST ONE", "1-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(2, R.drawable.test_church, "TEST TWO", "2-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(3, R.drawable.test_work, "TEST THREE", "3-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(4, R.drawable.test_bbq, "TEST FOUR", "4-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(5, R.drawable.test_church, "TEST FIVE", "5-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(6, R.drawable.test_work, "TEST SIX", "6-Sept-16 8:00pm"));
        data.add(new CarPoolEventEntity(7, R.drawable.test, "TEST SEVEN", "7-Sept-16 11:00pm"));

        return data;
    }

}





