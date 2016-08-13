package com.project.uoa.carpooling.fragments.carpool;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;

/**
 * Created by Chester on 10/08/2016.
 */
public class DetailsFragment extends Fragment {

    private TextView description;
    private TextView expText;
    private boolean isExpanded = false;
    private CardView eventInfoCard;

    // This adds to the EVENT INFORMATION card
    public void addEventDetails(View view) {

        ComplexEventEntity facebookEvent = ((CarpoolEventActivity) getActivity()).getFacebookEvent();

        // EventDetails:
        // EVENT_NAME
        TextView name = (TextView) view.findViewById(R.id.event_name);
        name.setText("Event: " + facebookEvent.getName());

        // EVENT_START_TIME
        TextView startDate = (TextView) view.findViewById(R.id.date_text);
        startDate.setText(facebookEvent.getLongStartDate());

        TextView startTime = (TextView) view.findViewById(R.id.time_text);
        startTime.setText(facebookEvent.getLongStartTime());

        // EVENT_LOCATION
        TextView location = (TextView) view.findViewById(R.id.location_placename);
        if (facebookEvent.getLocation().toString().equals("")) {
            location.setText("No location available");
        } else {
            location.setText("Location: " + facebookEvent.getLocation().toString());
        }

        // EVENT_DESCRIPTION
        description = (TextView) view.findViewById(R.id.event_description);
        if (facebookEvent.getDescription().equals("")) {
            description.setText("No description available.");
        } else {
            description.setText(facebookEvent.getDescription());
        }

        expText = (TextView) view.findViewById(R.id.expand_text);

        eventInfoCard = (CardView) view.findViewById(R.id.event_information_card);

        description.post(new Runnable() {
            @Override
            public void run() {

                int lineCount = description.getLineCount();
                if (lineCount > 2) {
                    eventInfoCard.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (isExpanded == false) {
                                isExpanded = true;
                                description.setMaxLines(Integer.MAX_VALUE);
                                expText.setVisibility(View.GONE);
                            } else {
                                isExpanded = false;
                                description.setMaxLines(2);
                                expText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    expText.setVisibility(View.GONE);
                }
            }
        });
    }
}