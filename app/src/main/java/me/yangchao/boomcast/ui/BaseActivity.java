package me.yangchao.boomcast.ui;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.yangchao.boomcast.R;

/**
 * Created by richard on 4/11/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

//    protected <T extends Fragment> T addFragment(Class<T> fragmentClass, @IdRes int containerViewId) {
//        return addFragment(fragmentClass, containerViewId, new Bundle());
//    }

//    protected <T extends Fragment> T addFragment(Class<T> fragmentClass, @IdRes int containerViewId) {
//        return addFragment(() -> {
//            try {
//                return fragmentClass.newInstance();
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }, containerViewId);
//    }

    protected <T extends Fragment> T addFragment(FragmentFactory<T> fragmentFactory, @IdRes int containerViewId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(containerViewId);

        if (fragment == null) {
            fragment = fragmentFactory.newInstance();

            fragmentManager.beginTransaction()
                    .add(containerViewId, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragment = fragmentFactory.newInstance();

            fragmentManager.beginTransaction()
                    .replace(containerViewId, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        return (T) fragment;
    }


//    protected <T extends Fragment> T addFragment(Class<T> fragmentClass, @IdRes int containerViewId, Bundle arguments) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(containerViewId);
//
//        if (fragment == null) {
//            try {
//                fragment = fragmentClass.newInstance();
//                fragment.setArguments(arguments);
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            fragmentManager.beginTransaction()
//                    .add(containerViewId, fragment)
//                    .commit();
//        }
//
//        return (T) fragment;
//    }

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
