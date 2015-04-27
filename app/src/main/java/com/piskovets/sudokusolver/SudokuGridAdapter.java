package com.piskovets.sudokusolver;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class SudokuGridAdapter extends BaseAdapter {
    private Activity context;
    private List<String> items;

    static class ViewHolder{
        public TextView title;
    }

    public SudokuGridAdapter(Activity context, List<String> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount(){
        return  items.size();
    }

    @Override
    public Object getItem(int location){
        return items.get(location);
    }

    @Override
    public long getItemId(int position){
        return position;
    }



    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater=context.getLayoutInflater();
            rowView=inflater.inflate(R.layout.grid_item,null);
            ViewHolder viewHolder=new ViewHolder();
            viewHolder.title=(TextView)rowView.findViewById(R.id.textView);
            rowView.setTag(viewHolder);
        }
        String b=items.get(position);
        int n=5;
        ViewHolder holder=(ViewHolder) rowView.getTag();

        if(position==0||position==3||position==6||position==27||position==30||position==33||position==54||position==57||position==60){
            rowView.setPadding(n,n,0,0);
        }
        if(position==1||position==4||position==7||position==28||position==31||position==34||position==55||position==58||position==61){
            rowView.setPadding(0,n,0,0);
        }
        if(position==2||position==5||position==8||position==29||position==32||position==35||position==56||position==59||position==62){
            rowView.setPadding(0,n,n,0);
        }
        if(position==9||position==12||position==15||position==36||position==39||position==42||position==63||position==66||position==69){
            rowView.setPadding(n,0,0,0);
        }
        if(position==11||position==14||position==17||position==38||position==41||position==44||position==65||position==68||position==71){
            rowView.setPadding(0,0,n,0);
        }
        if(position==18||position==21||position==24||position==45||position==48||position==51||position==72||position==75||position==78){
            rowView.setPadding(n,0,0,n);
        }
        if(position==19||position==22||position==25||position==46||position==49||position==52||position==73||position==76||position==79){
            rowView.setPadding(0,0,0,n);
        }
        if(position==20||position==23||position==26||position==47||position==50||position==53||position==74||position==77||position==80){
            rowView.setPadding(0,0,n,n);
        }
        holder.title.setText(b);
        holder.title.setBackgroundColor(Color.WHITE);
        if(holder.title.getText().toString().equals(""))
            holder.title.setTextColor(Color.BLUE);
        return rowView;
    }



}
