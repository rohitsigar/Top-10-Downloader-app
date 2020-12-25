package com.timbuchalka.top10downloader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
public class FeedAdaptor<T extends FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdaptor";
    private final int layoutResources;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    public FeedAdaptor(@NonNull Context context, int resource, List<T> applications) {
        super(context, resource);
        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResources = resource;
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            Log.d(TAG, "getView: new view is formed ");
            convertView = layoutInflater.inflate(layoutResources, parent, false);
            viewHolder = new ViewHolder(convertView);
           convertView.setTag(viewHolder);
        }else{
            Log.d(TAG, "getView: view si reused");
            viewHolder = (ViewHolder) convertView.getTag();
        }
       // TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       // TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
        //TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

        T currentApp= applications.get(position);
       // tvName.setText(currentApp.getName());
       // tvArtist.setText(currentApp.getArtist());
       // tvSummary.setText(currentApp.getSummary());
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText( currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }
    private  class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        ViewHolder( View v)
        {
            this.tvName=(TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary= (TextView) v.findViewById(R.id.tvSummary);
        }
    }
}
