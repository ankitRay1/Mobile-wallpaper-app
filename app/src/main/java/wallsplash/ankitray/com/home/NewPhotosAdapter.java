package wallsplash.ankitray.com.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import wallsplash.ankitray.com.bean.PhotosBean;
import wallsplash.ankitray.com.wallsplash.R;

public class NewPhotosAdapter extends RecyclerView.Adapter<NewPhotosAdapter.RecyclerVH> {
    Context context;
    ArrayList<PhotosBean> arrCateList = new ArrayList<>();
    OnPhotoSelectedListner onPhotoSelectedListner;
    PhotosBean catbean;



    public NewPhotosAdapter(Context context, ArrayList<PhotosBean> photosBeans) {
        this.context = context;
        this.arrCateList = photosBeans;
    }

    public void setOnCategorySelectedListner(OnPhotoSelectedListner onPhotoSelectedListner) {

        this.onPhotoSelectedListner = onPhotoSelectedListner;
    }

    public interface OnPhotoSelectedListner {
        void setOnPhotoSelatedListner(int position, PhotosBean dataBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_newphotos, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
      //  holder.tvTitle.setText(Html.fromHtml(arrCateList.get(position).getTitle()));
        Glide.with(context).load(arrCateList.get(position).getRegular())
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_placeholder_sq)
                .error(R.drawable.ic_placeholder_sq)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgview);
        Glide.with(context).load(arrCateList.get(position).getMedium())
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cv_user_image);
     /*   Picasso.get()
                .load(arrCateList.get(position).getRegular())
                .into(holder.imgview);*/
       /* Picasso.get()
                .load(arrCateList.get(position).getMedium())
                .into(holder.cv_user_image);*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onPhotoSelectedListner.setOnPhotoSelatedListner(position, arrCateList.get(position));

            }
        });
    }
   /* public void addPhotos(List<CollectionBean> photos){
        // int lastCount = getItemCount();
        arrCateList.addAll(photos);
        //   notifyItemRangeInserted(lastCount, photos.size());
    }
*/
    @Override
    public int getItemCount() {
        return arrCateList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        ImageView imgview;
        CircleImageView cv_user_image;
       // TextView tvTitle;

        public RecyclerVH(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);
            cv_user_image = (CircleImageView) itemView.findViewById(R.id.cv_user_image);
          //  tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}