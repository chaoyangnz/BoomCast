package me.yangchao.boomcast.ui;

import android.support.v4.app.Fragment;

/**
 * Created by richard on 4/11/17.
 */

public interface FragmentFactory<T extends Fragment> {

    T newInstance();
}
