package cs371m.dcd954.recordexpress;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongAdaptor extends ArrayAdapter<SongDict> {

    /*
    static class ViewHolder {
        TextView theTextView;

        public ViewHolder(View v) {
            theTextView = (TextView) v.findViewById(R.id.textID);
        }
    }
    */

    private LayoutInflater theInflater = null;
    //private String[] list_names;

    public SongAdaptor(Context context) {
        super(context, R.layout.song_row);
        //this.list_names = list_names;
        theInflater = LayoutInflater.from(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        if (convertView == null) {
            convertView = theInflater.inflate(R.layout.song_row, parent, false);
        }

        return bindView(position, convertView);
    }

    private View bindView(int position, View theView) {
        SongDict song = getItem(position);
        TextView textView = theView.findViewById(R.id.textID);
        textView.setText(song.name);

        return theView;
    }


    /*
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        SongAdaptor.ViewHolder vh = null;
        if (convertView == null) {
            convertView = theInflater.inflate(R.layout.song_row, parent, false);
            vh = new SongAdaptor.ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (SongAdaptor.ViewHolder) convertView.getTag();
        }

        SongDict songDict = getItem(position);
        vh.theTextView.setText(songDict.name);

        return convertView;
    }
    */
}
