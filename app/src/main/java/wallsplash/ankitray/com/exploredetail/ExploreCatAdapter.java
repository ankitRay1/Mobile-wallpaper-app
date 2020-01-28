package wallsplash.ankitray.com.exploredetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.ExploreCatBean;
import wallsplash.ankitray.com.wallsplash.R;

public class ExploreCatAdapter extends RecyclerView.Adapter<ExploreCatAdapter.RecyclerVH> {
    Context c;
    ArrayList<ExploreCatBean> exploreCatBeanList = new ArrayList<>();
    OnCategorySelectedListner onCategorySelectedListner;
    ExploreCatBean trendingBean;

    public ExploreCatAdapter(Context c, ArrayList<ExploreCatBean> exploreCatBeanList) {
        this.c = c;
        this.exploreCatBeanList = exploreCatBeanList;
    }

    public void setOnCategorySelectedListner(OnCategorySelectedListner onCategorySelectedListner) {

        this.onCategorySelectedListner = onCategorySelectedListner;
    }

    public interface OnCategorySelectedListner {
        void setOnCategorySelatedListner(int position, ExploreCatBean trendingBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_explorecat, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
        holder.listtext.setText(Html.fromHtml(exploreCatBeanList.get(position).getTitle()));
        if (exploreCatBeanList.get(position).isSelected()) {
            holder.listtext.setBackground(c.getResources().getDrawable(R.drawable.disablebutton));
            holder.listtext.setTextColor(c.getResources().getColor(R.color.colorWhite));

        } else {
            holder.listtext.setBackground(c.getResources().getDrawable(R.drawable.clickbutton));
            holder.listtext.setTextColor(c.getResources().getColor(R.color.colorBlack));

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onCategorySelectedListner.setOnCategorySelatedListner(position, exploreCatBeanList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return exploreCatBeanList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        TextView listtext;

        public RecyclerVH(View itemView) {
            super(itemView);
            listtext = (TextView) itemView.findViewById(R.id.listtext);
        }
    }
}