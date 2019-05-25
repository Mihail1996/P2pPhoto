package ist.meic.cmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ist.meic.cmu.R;
import ist.meic.cmu.utils.LoggerFactory;


public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoNameViewHolder> {
    private String TAG = this.getClass().getSimpleName();
    private List<String> mPhotos;
    private final Callback mCallback;
    private Picasso mPicasso;


    public void setPhotos(List<String> photos) {
        mPhotos = Collections.unmodifiableList(new ArrayList<>(photos));
        notifyDataSetChanged();
    }

    public List<String> getPhotos() {
        return mPhotos;
    }

    public interface Callback {
        void onPhotoClicked(String url);
    }

    public PhotosAdapter(Picasso picasso, Callback callback) {
        mCallback = callback;
        mPicasso = picasso;
    }

    @Override
    public long getItemId(int position) {
        return mPhotos.get(position).hashCode();
    }

    @Override
    public PhotoNameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.photo_item, viewGroup, false);
        return new PhotoNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoNameViewHolder metadataViewHolder, int i) {
        metadataViewHolder.bind(mPhotos.get(i));

    }

    @Override
    public int getItemCount() {
        return mPhotos == null ? 0 : mPhotos.size();
    }

    public class PhotoNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTextView;
        private final ImageView mImageView;
        private String mUrl;

        public PhotoNameViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageItem);
            mTextView = itemView.findViewById(R.id.textItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onPhotoClicked(mUrl);

        }

        public void bind(String url) {
            LoggerFactory.log(TAG + ": Initialized binding " + url);
            mUrl = url;
            String filename = url.substring(url.lastIndexOf('/') + 1);
            filename = filename.replace("?dl=0", "");
            filename = filename.replace("?dl=1", "");
            if (filename.length() > 36) {
                filename = filename.substring(36);
            }
            mTextView.setText(filename);
            mPicasso.load(url)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .error(R.drawable.ic_photo_black_24dp)
                    .into(mImageView);

            LoggerFactory.log(TAG + ": Finished binding " + url);
        }
    }
}
