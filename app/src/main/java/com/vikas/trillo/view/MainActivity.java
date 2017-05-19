package com.vikas.trillo.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vikas.trillo.R;
import com.vikas.trillo.model.AudioModel;
import com.vikas.trillo.presentor.ViewPagerContainerAdapter;
import com.vikas.trillo.utils.SessionManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vpFragmentMainContainer)
    ViewPager vpFragmentMainContainer;
    @BindView(R.id.tlFragmentMainContainer)
    TabLayout tlFragmentMainContainer;
    private ViewPagerContainerAdapter viewPagerContainerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();
        setTabWithViewPager();
    }

    private void setTabWithViewPager() {
        viewPagerContainerAdapter = new ViewPagerContainerAdapter(getSupportFragmentManager());
        vpFragmentMainContainer.setAdapter(viewPagerContainerAdapter);
        tlFragmentMainContainer.setupWithViewPager(vpFragmentMainContainer);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openUploadedAudio();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openUploadedAudio() {
        //here open the uploaded audio
        try {
            SessionManager sessionManager = new SessionManager(this);
            ArrayList<AudioModel> uploadAudioModels = sessionManager.getAudioList();
            if (uploadAudioModels != null && uploadAudioModels.size() > 0) {
                ArrayList<String> stringArrayList = new ArrayList<>();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
                for (AudioModel audioModel : uploadAudioModels) {
                    stringArrayList.add("Date: " + audioModel.getPayload().getCreated() + "\n" + "File: " + audioModel.getPayload().getFilename());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Uploaded Audio");
                builder.setAdapter(arrayAdapter, null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            } else {
                Toast.makeText(this, "No files uploaded yet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
