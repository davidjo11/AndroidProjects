package com.procrastinate.davidtheprocrastinator.gestionduparc;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by David on 19/11/2015.
 */
public class IssueTypeAdapter extends BaseAdapter{

    private final List<IssueType> list;
    private final Activity context;

    public IssueTypeAdapter(Activity context, List<IssueType> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView id;
        protected TextView title;
        protected TextView address;
        protected TextView lat, lng;
        protected IssueType type;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int location) {
        // TODO Auto-generated method stub
        return list.get(location);
    }

    @Override
    public long getItemId(int id) {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
