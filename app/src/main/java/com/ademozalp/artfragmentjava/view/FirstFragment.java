package com.ademozalp.artfragmentjava.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ademozalp.artfragmentjava.adapter.ArtAdapter;
import com.ademozalp.artfragmentjava.databinding.FragmentFirstBinding;
import com.ademozalp.artfragmentjava.model.modelArt;
import com.ademozalp.artfragmentjava.roomdb.ArtDao;
import com.ademozalp.artfragmentjava.roomdb.roomDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    modelArt Art;
    ArrayList<modelArt> artArrayList;
    roomDatabase db;
    ArtDao dao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtAdapter artAdapter;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(requireContext(), roomDatabase.class, "Arts").build();
        dao = db.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
        //return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();
    }

    public void getData(){
        compositeDisposable.add(dao.getArtWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FirstFragment.this::handleResponse));
    }

    private void handleResponse(List<modelArt> artList){
        if(artList.size() == 0){
            Toast.makeText(requireContext(),"Art List is empty",Toast.LENGTH_LONG).show();
        }else{
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            artAdapter = new ArtAdapter(artList);
            binding.recyclerView.setAdapter(artAdapter);
        }
    }
}