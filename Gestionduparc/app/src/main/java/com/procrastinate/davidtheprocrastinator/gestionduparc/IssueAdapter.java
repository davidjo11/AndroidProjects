package com.procrastinate.davidtheprocrastinator.gestionduparc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by David on 18/11/2015.
 */
public class IssueAdapter extends BaseAdapter {
    private final List<Issue> list;
    private final Activity context;

    public IssueAdapter(Activity context, List<Issue> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
		protected TextView id;
        protected TextView title;
		protected TextView type;
		protected TextView resolved;
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


	@TargetApi(Build.VERSION_CODES.M)
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // pour voir l'intérêt du viewHolder, mettre tout le test ci-dessous en commentaire et décommenter l'autre solution
        // dans les deux cas, faire bcp de scroll et regarder la charge de la mémoire

        // Solution AVEC viewHolder
		View view = null;
		if (convertView == null) {

			LayoutInflater inflator = context.getLayoutInflater();

			view = inflator.inflate(R.layout.list_item_layout, null);

			view.setClickable(true);

			// on pourrait aussi définir le listener dans l'activité associée
			// avec
			// ici :
			// view.setOnClickListener(myClickListener);

			// et dans l'activité,
			// public OnClickListener myClickListener = new OnClickListener() {
			// 		public void onClick(View v) {
			// 			//code .....
			// 		}
			// };

			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.title = (TextView) view.findViewById(R.id.itemTitle);
//			viewHolder.title.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
////					Log.d("clic", "nom");
//				}
//			});

			viewHolder.id = (TextView) view.findViewById(R.id.issueId);

			viewHolder.type = (TextView) view.findViewById(R.id.textView);

			viewHolder.resolved = (TextView) view.findViewById(R.id.textResolu);

//			viewHolder.selectionne = (CheckBox) view.findViewById(R.id.valide);
//			viewHolder.selectionne
//					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView,
//								boolean isChecked) {
//							Issue element = (Issue) viewHolder.selectionne
//									.getTag();
//							element.definirSelection(buttonView.isChecked());
//
//						}
//					});

//			view.setTag(viewHolder);
//			viewHolder.selectionne.setTag(list.get(position));
		} else {
			view = convertView;
//			((ViewHolder) view.getTag()).selectionne.setTag(list.get(position));
		}

        // fin solution AVEC viewHolder




        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(list.get(position).getTitle());
		holder.type.setText(list.get(position).getType().name());
        holder.id.setText(list.get(position).getId());
		String s = ""+list.get(position).isResolved();
		holder.resolved.setText(s);
		if(s.equals("Non résolu"))
			holder.resolved.setTextColor(context.getResources().getColor(R.color.red, null));
		else holder.resolved.setTextColor(context.getResources().getColor(R.color.green, null));


		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editIssue("" + list.get(position).getId());
			}
		});

        return view;
    }

	private void editIssue(String id) {
		Intent intent = new Intent(context,Issue_Activity.class);
		intent.putExtra("type", Tools.ISSUE_LAYOUT_MODE_EDIT);
		intent.putExtra("issueId", id);
		context.startActivity(intent);
	}

	private void clicMoi(View v) {

    }

	private void localize(String address){
		String add = address.trim().replace(' ','+');
		Uri u = Uri.parse("geo:0,0?q="+add+"?z=17");
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  u);
		context.startActivity(intent);
	}

	private void localize(double lat, double lng){
		Uri u = Uri.parse("geo:"+lat+","+lng+"?z=17");
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, u);
		context.startActivity(intent);
	}

	private void localize(String address, double lat, double lng){
		String add = address.trim().replace(' ','+');
		Uri u = Uri.parse("geo:"+lat+","+lng+"?q="+add+"?z=17");
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, u);
		context.startActivity(intent);
	}

}
