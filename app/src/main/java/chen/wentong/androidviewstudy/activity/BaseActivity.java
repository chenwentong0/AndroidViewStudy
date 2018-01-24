package chen.wentong.androidviewstudy.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by ${wentong.chen} on 18/1/24.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }

    protected abstract  @LayoutRes int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

}
