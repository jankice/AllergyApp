package com.task.allergyapp.data;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoginContext {

    static GoogleSignInAccount account;

    public static void setAccount(GoogleSignInAccount account) {
        LoginContext.account = account;
    }

    public static GoogleSignInAccount getAccount() {
        return account;
    }
}
