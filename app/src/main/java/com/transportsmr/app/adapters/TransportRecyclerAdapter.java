package com.transportsmr.app.adapters;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.transportsmr.app.R;
import com.transportsmr.app.TransportApp;
import com.transportsmr.app.model.ArrivalTransport;
import com.transportsmr.app.model.Transport;
import com.transportsmr.app.utils.BabushkaText;
import com.transportsmr.app.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirill on 12.12.2016.
 */
public class TransportRecyclerAdapter extends RecyclerView.Adapter<TransportRecyclerAdapter.ViewHolder> implements Filterable {
    final static int MAX_BUSES = 5;
    final static int MAX_BUSES_COMMERCIAL = 5;
    private final TransportApp app;
    private final SharedPreferences sPref;
    private Context context;
    private List<ArrivalTransport> arrival;
    private Filter transportFilter;

    public TransportRecyclerAdapter(Context context, Application application, List<ArrivalTransport> dataset) {
        this.app = application instanceof TransportApp ? (TransportApp) application : null;
        arrival = dataset;
        this.context = context;
        sPref = context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Filter getFilter() {
        if (transportFilter == null)
            transportFilter = new TransportFilter(this);
        return transportFilter;
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
        int markCommercial = sPref.getInt(Constants.SHARED_MARK_COMMERCIAL, Constants.DEFAULT_MARK_COMMERCIAL); //0-new line 1-color 2-none

        holder.number.setText(arrivalTransport.getNumber());
        holder.number.setBackground(ContextCompat.getDrawable(context, getBackgroundFromType(arrivalTransport.getType())));

        initHeader(holder,
                transports.get(0).getTime() + context.getString(R.string.arrival_minute),
                arrivalTransport.getType());

        holder.nextStop.setText(transports.get(0).getRemainingLength() + context.getString(R.string.length_to) + transports.get(0).getNextStopName());

        //next time
        ArrayList<BabushkaText.Piece> buses = new ArrayList<>();
        ArrayList<BabushkaText.Piece> busesCommercial = new ArrayList<>();
        ArrayList<BabushkaText.Piece> pieces = null;
        int i = 0;
        int iC = 0;
        BabushkaText.Piece.Builder builder = null;
        for (Transport transport : transports) {
            builder = new BabushkaText.Piece.Builder(" " + transport.getTime())
                    .textSize((int) context.getResources().getDimension(R.dimen.material_text_body1))
                    .textColor(context.getResources().getColor(R.color.darkblue2))
                    .style(Typeface.BOLD);
            pieces = null;
            if (app.isCommercialBus(transport.getkRID())) {
                if (markCommercial == 2) { //none mark
                    if (i++ < MAX_BUSES) {
                        pieces = buses;
                    }
                } else if (markCommercial == 0) { // new line
                    if (iC++ < MAX_BUSES_COMMERCIAL) {
                        pieces = busesCommercial;
                    }
                } else { //color
                    if (i++ < MAX_BUSES) {
                        pieces = buses;
                        builder.textColor(context.getResources().getColor(R.color.darkred));
                    }
                }
            } else {
                if (i++ < MAX_BUSES) {
                    pieces = buses;
                }
            }
            if (pieces != null) {
                pieces.add(builder.build());
                pieces.add(new BabushkaText.Piece.Builder(", ")
                        .textSize((int) context.getResources().getDimension(R.dimen.material_text_body1))
                        .build());
            }
        }

        initTimeView(buses, holder.nextTime);
        initTimeView(busesCommercial, holder.nextTimeCommercial);
    }

    private void initHeader(ViewHolder holder, String time, String type) {
        holder.header.reset();
        holder.header.addPiece(new BabushkaText.Piece.Builder(time.toUpperCase())
                .textColor(context.getResources().getColor(R.color.black))
                .textSize((int) context.getResources().getDimension(R.dimen.material_text_body1))
                .build()
        );

        holder.header.addPiece(new BabushkaText.Piece.Builder(type.toUpperCase())
                .textColor(context.getResources().getColor(R.color.gray))
                .textSize((int) context.getResources().getDimension(R.dimen.material_text_body1))
                .build()
        );
        holder.header.display();
    }

    private void initTimeView(ArrayList<BabushkaText.Piece> pieces, BabushkaText textView) {
        if (pieces.size() > 0) {
            pieces.get(pieces.size() - 1).setText(" ");
            textView.reset();
            textView.addPiece(new BabushkaText.Piece.Builder(context.getResources().getString(R.string.arrival_time_to_next))
                    .textColor(context.getResources().getColor(R.color.gray))
                    .textSize(-1)
                    .build()
            );
            textView.addPieces(pieces);

            textView.addPiece(new BabushkaText.Piece.Builder(context.getResources().getString(R.string.arrival_minutes))
                    .textColor(context.getResources().getColor(R.color.gray))
                    .textSize(-1)
                    .build()
            );
            textView.display();
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private int getBackgroundFromType(String type) {
        if (type.equals(context.getString(R.string.tram))) {
            return R.drawable.bg_red;
        } else if (type.equals(context.getString(R.string.metro))) {
            return R.drawable.bg_gray;
        } else if (type.equals(context.getString(R.string.troll))) {
            return R.drawable.bg_green;
        }

        return R.drawable.bg_blue;
    }

    @Override
    public int getItemCount() {
        return arrival.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.transport_time_type)
        BabushkaText header;
        @BindView(R.id.transport_next_stop)
        TextView nextStop;
        @BindView(R.id.transport_number)
        TextView number;
        @BindView(R.id.transport_next_time)
        BabushkaText nextTime;
        @BindView(R.id.transport_next_time_commercial)
        BabushkaText nextTimeCommercial;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    public static class TransportFilter extends Filter {

        private final TransportRecyclerAdapter adapter;
        private final List<ArrivalTransport> filteredList;

        private TransportFilter(TransportRecyclerAdapter adapter) {
            super();
            this.adapter = adapter;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ArrivalTransport> originalList = new LinkedList<>(adapter.arrival);
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for (final ArrivalTransport transport : originalList) {
                    if (constraint.toString().contains(transport.getType())) {
                        filteredList.add(transport);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.arrival.clear();
            adapter.arrival.addAll((ArrayList<ArrivalTransport>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
