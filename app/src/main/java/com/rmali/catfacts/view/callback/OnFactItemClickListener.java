package com.rmali.catfacts.view.callback;

import android.support.annotation.NonNull;

import com.rmali.catfacts.model.pojo.CatFactEntity;

/**
 * Created by rmali on 11/8/2017.
 */

public interface OnFactItemClickListener {
    void onFactItemCLicked(@NonNull CatFactEntity factEntity);
}
