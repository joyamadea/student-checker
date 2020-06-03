package id.ac.umn.studentchecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddMahasiswaActivity extends AppCompatActivity {
    Spinner angkatanSpinner,prodiSpinner;
    TextInputLayout nm,nim,b;
    EditText etnama, etnim, etbio;
    ProgressDialog mInsertProgress,mUploadProgress;
    Button btnInsert;
    DatabaseReference ref;
    ImageView imImg;
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
        setContentView(R.layout.activity_add_mahasiswa);

        angkatanSpinner = findViewById(R.id.angkatan_spinner);
        prodiSpinner = findViewById(R.id.prodi_spinner);
        btnInsert = findViewById(R.id.insertBtn);
        nm = findViewById(R.id.nama);
        etnama = nm.getEditText();
        nim = findViewById(R.id.nim);
        etnim = nim.getEditText();
        etnim.setTransformationMethod(null);
        b = findViewById(R.id.bio);
        etbio = b.getEditText();
        imImg = findViewById(R.id.img);

        mInsertProgress = new ProgressDialog(this);
        mUploadProgress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        imageLink = "default";



        etnama.setFilters(new InputFilter[] {new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end,
                                       Spanned dest, int etStart, int etEnd) {
                if (charSequence.equals("")) {
                    return charSequence;
                }
                if (charSequence.toString().matches("[a-zA-Z ]+")) {
                    return charSequence;
                }
                return "";
            }
        }});

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String username1=username.getText().toString();
                String nim=etnim.getText().toString();
                String nama=etnama.getText().toString();
                String bio=etbio.getText().toString();
                String angkatan=String.valueOf(angkatanSpinner.getSelectedItem());
                String prodi=String.valueOf(prodiSpinner.getSelectedItem());

                if(TextUtils.isEmpty(nim) || TextUtils.isEmpty(nama) || TextUtils.isEmpty(bio)) {
                    Toast.makeText(AddMahasiswaActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else{
                    mInsertProgress.setTitle("Inserting to database");
                    mInsertProgress.setMessage("Please wait :)");
                    mInsertProgress.show();
                    insert(nim,nama,bio,angkatan,prodi);
                }

            }
        });

        imImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });
    }


    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMahasiswaActivity.this);
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
                        Toast.makeText(AddMahasiswaActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        final Context context = getApplicationContext();
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        imageLink = uriTask.getResult().toString();
                        Picasso.get().load(imageLink).into(imImg);
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
                        Toast.makeText(AddMahasiswaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insert(String nim, String nama, String bio, String angkatan, String prodi){
        ref= FirebaseDatabase.getInstance().getReference("mhs").child(nim);

        HashMap<String, String> hashMap=new HashMap<>();
        hashMap.put("id",nim);
        hashMap.put("nama",nama);
        hashMap.put("nim",nim);
        hashMap.put("bio",bio);
        hashMap.put("angkatan",angkatan);
        hashMap.put("prodi",prodi);
        hashMap.put("pic",imageLink);

        ref.setValue(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                mInsertProgress.dismiss();
                Toast.makeText(AddMahasiswaActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddMahasiswaActivity.this,MainActivity2.class));
            }
        });
    }
}
