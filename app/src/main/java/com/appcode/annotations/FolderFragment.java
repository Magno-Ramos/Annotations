package com.appcode.annotations;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appcode.annotations.adapter.FolderAdapter;
import com.appcode.annotations.adapter.FolderListener;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.viewmodel.FoldersViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FolderFragment extends Fragment implements FolderListener {

    private FoldersViewModel foldersViewModel;
    private FolderAdapter folderAdapter;
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.emptyTextView)
    TextView tvEmptyFolders;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.folderAdapter = new FolderAdapter(context, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foldersViewModel = ViewModelProviders.of(this).get(FoldersViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        int xScreen = getResources().getDisplayMetrics().widthPixels;
        int item = getResources().getDimensionPixelSize(R.dimen.item_note_width);
        int spanCount = (xScreen / item);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recyclerView.setAdapter(folderAdapter);

        foldersViewModel.getAllFolders().observe(this, folders -> {

            tvEmptyFolders.setVisibility(folders.isEmpty() ? View.VISIBLE : View.GONE);
            folderAdapter.submitList(folders);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickFolder(Folder folder, View view) {

    }

    @Override
    public void onLongClickFolder(Folder folder, View view) {

    }

    @Override
    public void onClickOption(Folder folder, View view) {

    }
}
