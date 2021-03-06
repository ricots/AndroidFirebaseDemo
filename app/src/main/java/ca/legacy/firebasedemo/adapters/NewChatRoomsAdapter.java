package ca.legacy.firebasedemo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import ca.legacy.firebasedemo.models.Room;

/**
 * Created by matthewlagace on 14-08-11.
 */
public class NewChatRoomsAdapter extends BaseAdapter {
    private final int layout;
    private final LayoutInflater inflater;
    private final List<Room> models;

    /**
     * @param ref The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *            combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout This is the layout used to represent a single list item. You will be responsible for populating an
     *               instance of the corresponding view with the data from an instance of modelClass.
     * @param activity The activity containing the ListView
     */
    public NewChatRoomsAdapter(final Firebase ref, Activity activity, int layout, final String loggedUser) {
        this.layout = layout;
        this.inflater = activity.getLayoutInflater();
        this.models = new ArrayList<Room>();

        // Get all Chat Rooms the user has access to
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.getValue() != null) {
                    Room model = dataSnapshot.getValue(Room.class);
                    boolean addRoom = false;

                    if (model.getIsPrivate() && model.getCreatedBy().equals(loggedUser)) {
                        addRoom = true;
                    }
                    if (model.getIsPrivate() && model.getName().equals(loggedUser)) {
                        addRoom = true;
                        model.setOriginalName(model.getName());
                        model.setName(model.getCreatedBy());
                    }
                    if (!model.getIsPrivate()) {
                        addRoom = true;
                    }
                    if (addRoom) {
                        models.add(model);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        Room room = (Room) getItem(position);

        if (convertView == null) {
            view = inflater.inflate(layout, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.room = (TextView) view.findViewById(android.R.id.text1);
        viewHolder.position = position;
        viewHolder.room.setText(room.getName());

        view.setTag(viewHolder);
        return view;
    }

    // User ViewHolder for smooth scrolling lists
    static class ViewHolder {
        TextView room;
        int position;
    }
}
