package me.yangchao.boomcast.ui;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.yangchao.boomcast.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected void removeFragment(@IdRes int containerViewId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(containerViewId);
        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    protected <T extends Fragment> T addFragment(Fragment newFragment, @IdRes int containerViewId) {
        return addFragment(newFragment, containerViewId, false);
    }

    protected <T extends Fragment> T addFragment(Fragment newFragment, @IdRes int containerViewId, boolean pushToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(containerViewId);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            transaction.add(containerViewId, newFragment);
        } else {
            transaction.replace(containerViewId, newFragment);
        }
        if(pushToStack) transaction.addToBackStack(null);

        transaction.commit();

        return (T) newFragment;
    }

    protected void addToolbar() {
        addToolbar(false);
    }

    protected void addToolbar(boolean homeAsMenu) {
        // action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(homeAsMenu) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
