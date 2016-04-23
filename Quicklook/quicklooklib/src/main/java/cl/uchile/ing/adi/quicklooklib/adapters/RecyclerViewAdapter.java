package cl.uchile.ing.adi.quicklooklib.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment.OnListFragmentInteractionListener;
import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.items.FolderItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FileItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    protected final List<BaseItem> mValues;
    protected final OnListFragmentInteractionListener mListener;

    public RecyclerViewAdapter(List<BaseItem> items, OnListFragmentInteractionListener listener) {
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
        holder.mTextView.setText(holder.mItem.getName());
        String subTextMessage = holder.mItem.getFormattedType()+
                (!(holder.mItem instanceof FolderItem) ? (" - " + holder.mItem.getFormattedSize()) : "");
        holder.mSubTextView.setText(subTextMessage);
        holder.mImageView.setImageResource(holder.mItem.getImage());
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
        public BaseItem mItem;

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
        mListener.makeTransition(holder.mItem,true);
    }

}
