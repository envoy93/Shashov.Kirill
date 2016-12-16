package com.transportsmr.app.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.transportsmr.app.R;
import com.transportsmr.app.model.ArrivalTransport;
import com.transportsmr.app.model.Stop;
import com.transportsmr.app.model.Transport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 12.12.2016.
 */
public class TransportRecyclerAdapter extends RecyclerView.Adapter<TransportRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ArrivalTransport> arrival;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView nextStop;
        private TextView nextTime;
        private TextView number;
        private TextView type;

        public ViewHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.transport_number);
            type = (TextView) v.findViewById(R.id.transport_type);
            nextTime = (TextView) v.findViewById(R.id.transport_next_time);
            time = (TextView) v.findViewById(R.id.transport_time);
            nextStop = (TextView) v.findViewById(R.id.transport_next_stop);
        }

    }

    public TransportRecyclerAdapter(Context context, List<ArrivalTransport> dataset) {
        arrival = dataset;
        this.context = context;
    }

    @Override
    public TransportRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_transport_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ArrivalTransport arrivalTransport = arrival.get(position);
        final List<Transport> transports = arrivalTransport.getTransports();
        holder.number.setText(arrivalTransport.getNumber());

        holder.number.setBackground(ContextCompat.getDrawable(context, getBackgroundFromType(arrivalTransport.getType())));
        holder.type.setText(arrivalTransport.getType());
        StringBuilder sb = new StringBuilder(" ");

        for (int i = 0; (i < transports.size()) && (i < 5); i++) {
            sb.append(" ").append(transports.get(i).getTime()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        holder.nextTime.setText(sb.toString());
        holder.nextStop.setText(transports.get(0).getRemainingLength() + context.getString(R.string.length_to) + transports.get(0).getNextStopName());
        holder.time.setText(transports.get(0).getTime() + context.getString(R.string.arrival_minute));
    }

    private int getBackgroundFromType(String type){
        if (type.equals(context.getString(R.string.tram))) {
            return R.drawable.bg_red;
        } else if (type.equals(context.getString(R.string.metro))){
            return R.drawable.bg_gray;
        } else if (type.equals(context.getString(R.string.troll))){
            return R.drawable.bg_green;
        }

        return R.drawable.bg_blue;
    }

    @Override
    public int getItemCount() {
        return arrival.size();
    }
}
