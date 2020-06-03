package id.ac.umn.studentchecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    TextView tv,tNama,tNim,tBio,tAngkatan,tProdi;
    EditText eNim,eNama,eBio;
    Spinner eAngkatan,eProdi;
    ProgressDialog mEditProgress,mUploadProgress;
    String id;
    DatabaseReference reference;
    ImageView imView;
    Button btnDel,btnEdit;
    boolean flagEdit=false;
    DatabaseReference ref;
    Uri image_uri;
    StorageReference storageReference;
    String imageLink;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        id = intent.getStringExtra("nimy") ;

        tNama = findViewById(R.id.nama);
        tNim = findViewById(R.id.nim);
        tBio = findViewById(R.id.bio);
        tAngkatan = findViewById(R.id.angkatan);
        tProdi = findViewById(R.id.prodi);
        imView = findViewById(R.id.img);

        //eNim = findViewById(R.id.nimEdit);
        eNama = findViewById(R.id.namaEdit);
        eBio = findViewById(R.id.bioEdit);
        eAngkatan = findViewById(R.id.angkatanEdit);
        eProdi = findViewById(R.id.prodiEdit);

        visibilityView();

        btnDel = findViewById(R.id.delBtn);
        btnEdit = findViewById(R.id.editBtn);

        mEditProgress = new ProgressDialog(this);
        mUploadProgress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flagEdit){
                    btnDel.setText("Save Changes");
                    btnEdit.setText("Cancel");
                    flagEdit = true;
                    visibilityEdit();
                }
                else if(flagEdit){
                    btnDel.setText("Delete Mahasiswa");
                    btnEdit.setText("Edit Mahasiswa");
                    flagEdit = false;
                    visibilityView();
                }


            }
        });


        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flagEdit){

                    showDelDialog();
                }
                else if(flagEdit){
                    if(TextUtils.isEmpty(eNama.getText().toString()) || TextUtils.isEmpty(eBio.getText().toString())) {
                        Toast.makeText(DetailActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mEditProgress.setTitle("Updating Mahasiswa");
                        mEditProgress.setMessage("Please wait :)");
                        mEditProgress.show();
                        editUserData();
                    }

                }

            }
        });

        imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagEdit){
                    showImagePicDialog();
                }

            }
        });

        getUserData();
    }

    void visibilityEdit(){
        tNama.setVisibility(View.GONE);
        // tNim.setVisibility(View.GONE);
        tBio.setVisibility(View.GONE);
        tAngkatan.setVisibility(View.GONE);
        tProdi.setVisibility(View.GONE);

        eNama.setVisibility(View.VISIBLE);
        //eNim.setVisibility(View.VISIBLE);
        eBio.setVisibility(View.VISIBLE);
        eAngkatan.setVisibility(View.VISIBLE);
        eProdi.setVisibility(View.VISIBLE);
    }

    void visibilityView(){
        tNama.setVisibility(View.VISIBLE);
        tNim.setVisibility(View.VISIBLE);
        tBio.setVisibility(View.VISIBLE);
        tAngkatan.setVisibility(View.VISIBLE);
        tProdi.setVisibility(View.VISIBLE);

        eNama.setVisibility(View.GONE);
        //eNim.setVisibility(View.GONE);
        eBio.setVisibility(View.GONE);
        eAngkatan.setVisibility(View.GONE);
        eProdi.setVisibility(View.GONE);
    }

    void showDelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        // Add the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteUserData();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Are you sure you want to delete this mahasiswa?");

        // Create the AlertDialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //camera
//                    if(!checkCameraPermission()){
//                        requestCameraPermission();
//                    }
//                    else{
//                        pickFromCamera();
//                    }
                    pickFromCamera();
                }else if(which == 1){
//                    if(!checkStoragePermission()){
//                        requestStoragePermission();
//                    }
//                    else{
                    pickFromGallery();
//                    }
                }
            }
        });

        builder.create().show();
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                uploadProfile(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadProfile(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadProfile(final Uri uri) {
        // String filePathAndName = storagePath + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(System.currentTimeMillis()+"."+getExtension(uri));
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //upload image
                        Toast.makeText(DetailActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        final Context context = getApplicationContext();
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        imageLink = uriTask.getResult().toString();
                        Picasso.get().load(imageLink).into(imView);
                        // databaseReference.child(usrEmail).child("profilePhoto").setValue(link);
                        Uri downloadUri = uriTask.getResult();
                        mUploadProgress.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        mUploadProgress.setTitle("Uploading Image");
                        mUploadProgress.setMessage("Please wait :)");
                        mUploadProgress.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void editUserData(){
        ref= FirebaseDatabase.getInstance().getReference("mhs").child(id);

        HashMap<String, String> hashMap=new HashMap<>();
        hashMap.put("id",id);
        hashMap.put("nama",eNama.getText().toString());
        hashMap.put("nim",id);
        hashMap.put("bio",eBio.getText().toString());
        hashMap.put("angkatan",String.valueOf(eAngkatan.getSelectedItem()));
        hashMap.put("prodi",String.valueOf(eProdi.getSelectedItem()));
        hashMap.put("pic",imageLink);

        ref.setValue(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                mEditProgress.dismiss();
                Toast.makeText(DetailActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                flagEdit = false;
                btnDel.setText("Delete Mahasiswa");
                btnEdit.setText("Edit Mahasiswa");
                visibilityView();
                // startActivity(new Intent(DetailActivity.this,FirstFragment.class));
            }
        });
    }

    void deleteUserData(){
        reference= FirebaseDatabase.getInstance().getReference("mhs").child(id);
        reference.removeValue();
        Toast.makeText(this, "Mahasiswa Successfully Deleted", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(DetailActivity.this,FirstFragment.class);
        startActivity(i);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    void getUserData(){
        reference= FirebaseDatabase.getInstance().getReference("mhs").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Mhs mhs = dataSnapshot.getValue(Mhs.class);
                tNama.setText(mhs.getNama());
                tNim.setText(mhs.getNim());
                tBio.setText(mhs.getBio());
                tAngkatan.setText(mhs.getAngkatan());
                tProdi.setText(mhs.getProdi());

                eNama.setText(mhs.getNama());
                // eNim.setText(mhs.getNim());
                eBio.setText(mhs.getBio());
//                eAngkatan.setText(mhs.getAngkatan());
//                eProdi.setText(mhs.getProdi());

                String angaktanCompare = mhs.getAngkatan();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.angkatan_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eAngkatan.setAdapter(adapter);
                if (angaktanCompare != null) {
                    int spinnerPosition = adapter.getPosition(angaktanCompare);
                    eAngkatan.setSelection(spinnerPosition);
                }

                String prodiCompare = mhs.getProdi();
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.prodi_array, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eProdi.setAdapter(adapter1);
                if (prodiCompare != null) {
                    int spinnerPosition = adapter1.getPosition(prodiCompare);
                    eProdi.setSelection(spinnerPosition);
                }

                if(mhs.getPic().equals("default")){
                    imView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(String.valueOf(mhs.getPic())).into(imView);
                }

                imageLink = mhs.getPic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
