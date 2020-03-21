package com.task.allergyapp.ui.AllergyCheck.allergen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllergenViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllergenViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Allergen fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}