package mmconsultoria.co.mz.mbelamova.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mmconsultoria.co.mz.mbelamova.model.Token;

public class MyFirebaseIdService {
   /* @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken=FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refreshedToken);

    }*/


//    @Override
//    public void onNewToken(String token) {
//        super.onNewToken(token);
//
//        String refreshedToken=token;
//        updateTokenToServer(refreshedToken);
//
//    }

    private void updateTokenToServer(String refreshedToken) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");

        Token token=new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        }
    }
}
