package wallsplash.ankitray.com.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.PhotosBean;
import wallsplash.ankitray.com.bean.RelatedBean;
import wallsplash.ankitray.com.wallsplash.R;

public class RelatedPhotosAdapter extends RecyclerView.Adapter<RelatedPhotosAdapter.RecyclerVH> {
    Context context;
    ArrayList<RelatedBean> relatedList = new ArrayList<>();
    OnPhotoSelectedListner onPhotoSelectedListner;
    PhotosBean catbean;



    public RelatedPhotosAdapter(Context context, ArrayList<RelatedBean> relatedBeans) {
        this.context = context;
        this.relatedList = relatedBeans;
    }

    public void setOnCategorySelectedListner(OnPhotoSelectedListner onPhotoSelectedListner) {

        this.onPhotoSelectedListner = onPhotoSelectedListner;
    }

    public interface OnPhotoSelectedListner {
        void setOnPhotoSelatedListner(int position, RelatedBean relatedBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_related, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
      //  holder.tvTitle.setText(Html.fromHtml(arrCateList.get(position).getTitle()));
        Glide.with(context).load(relatedList.get(position).getRegular())
                .placeholder(R.drawable.ic_placeholder_photos)
                .error(R.drawable.ic_placeholder_photos)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onPhotoSelectedListner.setOnPhotoSelatedListner(position, relatedList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return relatedList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        ImageView imgview;


        public RecyclerVH(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);

        }
    }
}