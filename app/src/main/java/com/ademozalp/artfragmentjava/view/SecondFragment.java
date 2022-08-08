package com.ademozalp.artfragmentjava.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ademozalp.artfragmentjava.R;
import com.ademozalp.artfragmentjava.databinding.FragmentSecondBinding;
import com.ademozalp.artfragmentjava.model.modelArt;
import com.ademozalp.artfragmentjava.roomdb.ArtDao;
import com.ademozalp.artfragmentjava.roomdb.roomDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SecondFragment extends Fragment {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ImageView imageView;
    Bitmap selectedImage;
    TextView nameTxt;
    TextView artistTxt;
    TextView yearTxt;
    roomDatabase db;
    ArtDao dao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String info ="";
    modelArt artFromMain;
    SQLiteDatabase database;


    private FragmentSecondBinding binding;
    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(requireContext(), roomDatabase.class, "Arts").build();
        dao =db.artDao();
        saveLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container,false);
        View view = binding.getRoot();
        return view;
        // return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = binding.button;
        imageView = binding.imageView;
        nameTxt = binding.txtartname;
        artistTxt = binding.txtartistname;
        yearTxt = binding.txtyear;
        Button del = binding.button2;

        database = requireActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);

        if(getArguments() != null){
            info = SecondFragmentArgs.fromBundle(getArguments()).getInfo();
        }else{
            info = "new";
        }

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(view);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        if(info.equals("new")){
            binding.button2.setVisibility(View.GONE);
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.select);
            binding.txtartname.setText("");
            binding.txtartistname.setText("");
            binding.txtyear.setText("");
        }else{
            binding.button.setVisibility(View.GONE);
            binding.button2.setVisibility(View.VISIBLE);
            int artId = SecondFragmentArgs.fromBundle(getArguments()).getArtId();

            compositeDisposable.add(dao.getArtById(artId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SecondFragment.this::handleResponseFromDb));
        }
    }

    private void handleResponseFromDb(modelArt Art){
        artFromMain = Art;
        binding.txtartname.setText(Art.artname);
        binding.txtartistname.setText(Art.artistname);
        binding.txtyear.setText(Art.year);

        Bitmap savedImage = BitmapFactory.decodeByteArray(Art.image, 0, Art.image.length);
        binding.imageView.setImageBitmap(savedImage);
    }
    public void save(View view){
        String name = nameTxt.getText().toString();
        String artist = artistTxt.getText().toString();
        String year = yearTxt.getText().toString();
        selectedImage = makeSmallerImage(selectedImage, 300);

        if(!name.matches("") && !artist.matches("") && !year.matches("") && selectedImage != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG,50,stream);
            byte[] byteArray = stream.toByteArray();
            modelArt Art = new modelArt(byteArray, name, artist, year);

            compositeDisposable.add(dao.insert(Art)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SecondFragment.this::handleResponse));
        }else{
            Toast.makeText(requireContext(),"Please fill in the fields", Toast.LENGTH_LONG).show();
        }
    }

    private void delete(View view){
        compositeDisposable.add(dao.delete(artFromMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SecondFragment.this::handleResponse));
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    private void handleResponse(){

        NavDirections directions = SecondFragmentDirections.actionSecondFragmentToFirstFragment();
        Navigation.findNavController(requireView()).navigate(directions);
    }

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for select a image",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // request permission with description
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                });
            }
            else{
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            // go to gallery
            Intent intent =new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        }
    }

    public void saveLauncher(){
        permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // go to gallery -> permission granted
                    Intent intent =new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }
                else{
                    // permission denied
                    Toast.makeText(requireContext(), "Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intentFromResult = result.getData();
                if(intentFromResult != null){
                    Uri imageData = intentFromResult.getData();
                    //imageView.setImageURI(imageData);
                    try {
                        if(Build.VERSION.SDK_INT >= 28){
                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                            selectedImage = ImageDecoder.decodeBitmap(source);
                            imageView.setImageBitmap(selectedImage);
                        }
                        else{
                            selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);
                            imageView.setImageBitmap(selectedImage);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        binding = null;
    }
}