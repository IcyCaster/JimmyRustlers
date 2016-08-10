package com.project.uoa.carpooling.fragments.carpool;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
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

        CardView eventInfoCard = (CardView) view.findViewById(R.id.event_information_card);
        eventInfoCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isExpanded == false) {
                    isExpanded = true;
                    description.setMaxLines(Integer.MAX_VALUE);
//                    description.setEllipsize(null);
                    expText.setVisibility(View.GONE);
                }
                else {
                    isExpanded = false;
                    description.setMaxLines(2);
//                    description.setEllipsize(TextUtils.TruncateAt.END);
                    expText.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}
