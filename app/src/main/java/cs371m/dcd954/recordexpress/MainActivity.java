package cs371m.dcd954.recordexpress;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private BottomNavigationView navigationView;

    private final String homeTag = "HomeFrag";
    private final String playTag = "PlayFrag";
    private final String editTag = "EditFrag";

    private Fragment homeFragment;
    private Fragment playFragment;
    private Fragment editFragment;
    private Fragment prevFragment;

    /* Taken from demo-demo/demolistviewfrag/MainActivity/showFragment */
    private void showFragment (Fragment fragment, String fragmentTag) {
        FragmentTransaction ft;
        // Start Fragment transactions
        ft = getSupportFragmentManager().beginTransaction();
        ft.detach(prevFragment);
        ft.attach(fragment);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /* Taken from demo-demo/demolistviewfrag/MainActivity/BottomNavigationView */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle("Home");
                    showFragment(homeFragment, homeTag);
                    prevFragment = homeFragment;
                    return true;
                case R.id.navigation_dashboard:
                    actionBar.setTitle("Play");
                    showFragment(playFragment, playTag);
                    prevFragment = playFragment;
                    return true;
                case R.id.navigation_notifications:
                    actionBar.setTitle("Edit");
                    showFragment(editFragment, editTag);
                    prevFragment = editFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        //actionBar.setIcon(R.drawable.ic_insert_emoticon_black_24dp);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigationView.getMenu().getItem(0).setChecked(true);
        // Create all fragments up front, but only display one at a time
        homeFragment = new HomeFrag();
        playFragment = new PlayFrag();
        editFragment = new EditFrag();
        // Tell the fragment manager about all fragments and that all fragments
        // are overlayed on the frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame, homeFragment, homeTag);
        // As odd as it is to add and then detach the fragment, it must be done to
        // associate the fragment with the frame.  It is insufficien to add the fragment
        // and then hide/show it, because two fragments can't be part of the same
        // view hierarchy, even if one of them is hidden.  The ones not displayed must
        // be detached from the view hierarchy
        ft.add(R.id.frame, playFragment, playTag);
        ft.detach(playFragment);
        ft.add(R.id.frame, editFragment, editTag);
        ft.detach(editFragment);
        ft.commit();
        prevFragment = homeFragment;

        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*
                DynamicList dynamicList = (DynamicList) dynamicFragment;
                dynamicList.addRandItem();
                swipeLayout.setRefreshing(false);
                */
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.setting:
                /*
                if( dynamicFragment != null ) {
                    DynamicList dynamicList = (DynamicList) dynamicFragment;
                    dynamicList.clear();
                }
                break;
                */
            case R.id.exit:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
