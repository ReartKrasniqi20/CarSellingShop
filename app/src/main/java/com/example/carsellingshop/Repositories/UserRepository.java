package com.example.carsellingshop.Repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.Map;

public class UserRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final CollectionReference users =
            FirebaseFirestore.getInstance().collection("users");

    private String uidOrThrow() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) throw new IllegalStateException("Not signed in");
        return u.getUid();
    }

    /** Read the signed-in user’s profile document. */
    public Task<DocumentSnapshot> getMyProfile() {
        try { return users.document(uidOrThrow()).get(); }
        catch (IllegalStateException e) { return Tasks.forException(e); }
    }

    /** Create/merge fields into the signed-in user’s document. */
    public Task<Void> upsertMyProfile(Map<String, Object> data) {
        try {
            String uid = uidOrThrow();
            return users.document(uid).set(data, SetOptions.merge());
        } catch (IllegalStateException e) {
            return Tasks.forException(e);
        }
    }

    /** Update specific fields (e.g., displayName, phone). */
    public Task<Void> updateMyFields(Map<String, Object> updates) {
        try {
            String uid = uidOrThrow();
            return users.document(uid).update(updates);
        } catch (IllegalStateException e) {
            return Tasks.forException(e);
        }
    }
}