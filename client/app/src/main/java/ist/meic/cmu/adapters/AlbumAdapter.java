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

/**
 * Adapter for albums
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumNameViewHolder> {
    private List<String> mAlbums;
    private final Callback mCallback;
    private Picasso mPicasso;

    public void setAlbums(List<String> files) {
        mAlbums = Collections.unmodifiableList(new ArrayList<>(files));
        notifyDataSetChanged();
    }

    public interface Callback {
        void onAlbumClicked(String name);
    }

    public AlbumAdapter(Picasso picasso, Callback callback) {
        mCallback = callback;
        mPicasso = picasso;
    }

    @Override
    public long getItemId(int position) {
        return mAlbums.get(position).hashCode();
    }

    @Override
    public AlbumNameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.album_item, viewGroup, false);
        return new AlbumNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumNameViewHolder metadataViewHolder, int i) {
        metadataViewHolder.bind(mAlbums.get(i));
    }

    @Override
    public int getItemCount() {
        return mAlbums == null ? 0 : mAlbums.size();
    }

    public class AlbumNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTextView;
        private final ImageView mImageView;
        private String mName;

        public AlbumNameViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_placeholder);
            mTextView = itemView.findViewById(R.id.album_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onAlbumClicked(mName);

        }

        public void bind(String name) {
            mName = name;
            mTextView.setText(mName);
            mPicasso.load(R.drawable.album_icon).into(mImageView);
        }
    }
}
