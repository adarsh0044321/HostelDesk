package com.adarshsingh.adminhosteldesk.data.api;

import android.content.Context;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;

    public AuthInterceptor(Context context) {
        this.sessionManager = SessionManager.getInstance(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = sessionManager.getToken();

        if (token != null && !token.isEmpty()) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json");
            return chain.proceed(builder.build());
        }

        return chain.proceed(original);
    }
}
