package cl.uchile.ing.adi.quicklooklib.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment.OnListFragmentInteractionListener;
import cl.uchile.ing.adi.quicklooklib.fragments.items.AbstractItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.DefaultItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ZipItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DefaultItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ZipRecyclerViewAdapter extends RecyclerView.Adapter<ZipRecyclerViewAdapter.ViewHolder> {

    protected final List<AbstractItem> mValues;
    protected final OnListFragmentInteractionListener mListener;

    public ZipRecyclerViewAdapter(List<AbstractItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_fileitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTextView.setText(mValues.get(position).getName());
        holder.mSubTextView.setText(mValues.get(position).getPath());
        holder.mImageView.setImageResource(mValues.get(position).getImage());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    clickAction(holder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextView;
        public final TextView mSubTextView;
        public final ImageView mImageView;
        public AbstractItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = (TextView) view.findViewById(R.id.item_text);
            mSubTextView = (TextView) view.findViewById(R.id.item_subtext);
            mImageView = (ImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }

    public void clickAction(ViewHolder holder) {
        AbstractItem item = holder.mItem;
        String name = item.getName();
        String path = item.getPath();
        long size = item.getSize();
        String type = "application/zip";
        ZipItem newItem = (ZipItem)ItemFactory.getInstance().createItem(path, type, name, size);
        if (item.isFolder()) {
            Log.d("ZipRecy", "Es carpeta!");
            mListener.onListFragmentInteraction(newItem);
        } else {
            Log.d("ZipRecy", "NO es carpeta!");
            mListener.onListFragmentExtraction(newItem);
        }
    }

}
