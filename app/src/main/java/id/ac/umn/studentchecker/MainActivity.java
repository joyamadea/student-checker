package id.ac.umn.studentchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

//    private MhsAdapter mhsAdapter;
//    private List<Mhs> mMhs;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        readUsers();
    }

    private void readUsers(){
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
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_mhs, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Mhs model) {
                holder.setNama(model.getNama());
                holder.setNim(model.getNim());
                holder.setImg(model.getPic());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
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
                Glide.with(getApplicationContext()).load(string).into(img);
            }
        }

    }



}
