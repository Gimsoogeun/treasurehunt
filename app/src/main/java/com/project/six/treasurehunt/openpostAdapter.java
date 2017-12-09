package com.project.six.treasurehunt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 발견한 게시글의 정보들을 activity에 listview로 나타내기위해 사용하는 adapter입니다.
 */

public class openpostAdapter extends ArrayAdapter<postContext> {
    public openpostAdapter(@NonNull Context context, int resource) {
        super(context, resource);

    }
    //view로 postContext의 정보들을 나타냅니다. 이때 layout은 openpostitem.xml
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
