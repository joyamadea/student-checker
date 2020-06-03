package id.ac.umn.studentchecker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class FirstFragment extends Fragment {
    private RecyclerView recyclerView;
    Button searchBtn;
    EditText searchText;
    Context context;
    //    private MhsAdapter mhsAdapter;
//    private List<Mhs> mMhs;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    boolean flagNim=false,flagNama=false;

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        recyclerView = view.findViewById(R.id.list_2);

//        Ascending
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchText = view.findViewById(R.id.searchText);
        searchBtn = view.findViewById(R.id.searchBtn);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
//        recyclerView.setLayoutManager(layoutManager);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texty = searchText.getText().toString();
                search(texty);
            }
        });
        setHasOptionsMenu(true);
        readUsers();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sortName:
                if(flagNama){
                    flagNama = false;
                    sort_nama("desc");
                }
                else if(!flagNama){
                    flagNama = true;
                    sort_nama("asc");
                }
                return true;
            case R.id.sortNim:
                if(flagNim){
                    flagNim = false;
                    sort_nim("desc");
                }
                else if(!flagNim){
                    flagNim = true;
                    sort_nim("desc");
                }
                return true;
            case R.id.aboutMe:
                return false;
            case R.id.logout:
                return false;
        }
        return false;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void readUsers(){
        adapter = null;
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("mhs");

        FirebaseRecyclerOptions<Mhs> options =
                new FirebaseRecyclerOptions.Builder<Mhs>()
                        .setQuery(query, new SnapshotParser<Mhs>() {
                            @NonNull
                            @Override
                            public Mhs parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Mhs(snapshot.child("nim").getValue().toString(),
                                        snapshot.child("nama").getValue().toString(),
                                        snapshot.child("pic").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Mhs, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_mhs, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final Mhs model) {
                holder.setNama(model.getNama());
                holder.setNim(model.getNim());
                holder.setImg(model.getPic());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), String.valueOf(model.getId()), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra("nimy",String.valueOf(model.getId()));
                        startActivity(i);
                    }
                });
            }

        };


        recyclerView.setAdapter(adapter);
    }

    public void sort_nama(String type){
        if(type == "desc"){
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
        }else if(type == "asc"){
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("mhs").orderByChild("nama");

        FirebaseRecyclerOptions<Mhs> options =
                new FirebaseRecyclerOptions.Builder<Mhs>()
                        .setQuery(query, new SnapshotParser<Mhs>() {
                            @NonNull
                            @Override
                            public Mhs parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Mhs(snapshot.child("nim").getValue().toString(),
                                        snapshot.child("nama").getValue().toString(),
                                        snapshot.child("pic").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .build();


        adapter.updateOptions(options);
    }

    public void sort_nim(String type){
        if(type == "desc"){
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
        }else if(type == "asc"){
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("mhs").orderByChild("nim");

        FirebaseRecyclerOptions<Mhs> options =
                new FirebaseRecyclerOptions.Builder<Mhs>()
                        .setQuery(query, new SnapshotParser<Mhs>() {
                            @NonNull
                            @Override
                            public Mhs parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Mhs(snapshot.child("nim").getValue().toString(),
                                        snapshot.child("nama").getValue().toString(),
                                        snapshot.child("pic").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .build();


        adapter.updateOptions(options);
    }

    public void search(String key){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("mhs").orderByChild("nama").startAt(key).endAt("\uf8ff");

        FirebaseRecyclerOptions<Mhs> options =
                new FirebaseRecyclerOptions.Builder<Mhs>()
                        .setQuery(query, new SnapshotParser<Mhs>() {
                            @NonNull
                            @Override
                            public Mhs parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Mhs(snapshot.child("nim").getValue().toString(),
                                        snapshot.child("nama").getValue().toString(),
                                        snapshot.child("pic").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .build();


        adapter.updateOptions(options);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout root;
        public TextView nim,nama;
        public ImageView img;

        public ViewHolder(View itemView){
            super(itemView);
            root = itemView.findViewById(R.id.mahasiswa_list);
            img = itemView.findViewById(R.id.list_img);
            nim = itemView.findViewById(R.id.list_nim);
            nama = itemView.findViewById(R.id.list_nama);

        }

        public void setNim(String string) {
            nim.setText(string);
        }


        public void setNama(String string) {
            nama.setText(string);
        }

        public void setImg(String string){
            if(string.equals("default")){
                img.setImageResource(R.mipmap.ic_launcher);
            } else{
                Picasso.get().load(string).into(img);
            }
        }

    }
}
