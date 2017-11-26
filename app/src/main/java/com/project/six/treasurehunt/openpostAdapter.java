package com.project.six.treasurehunt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by gunhe on 2017-11-24.
 */

public class openpostAdapter extends ArrayAdapter<postContext> {
    public openpostAdapter(@NonNull Context context, int resource) {
        super(context, resource);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.openpostitem, null);

            viewHolder = new ViewHolder();
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.oposttitle);
            viewHolder.mWriterName = (TextView) convertView.findViewById(R.id.owritername);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        postContext post = getItem(position);
        viewHolder.mTitle.setText(post.title);
        viewHolder.mWriterName.setText(post.writerName);
       // viewHolder.mTxtTime.setText(mSimpleDateFormat.format(chatData.time));

        return convertView;
    }

    private class ViewHolder {
        private TextView mTitle;
        private TextView mWriterName;
    }

}
