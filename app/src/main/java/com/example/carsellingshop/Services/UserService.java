package com.example.carsellingshop.Services;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final CollectionReference users =
            FirebaseFirestore.getInstance().collection("users");

    /** Current FirebaseAuth user (may be null). */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /** Ensure a basic user doc exists: {uid, email, displayName?}. Safe to call on login. */
    public Task<Void> ensureUserDoc() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return Tasks.forException(new IllegalStateException("Not signed in"));
        Map<String, Object> base = new HashMap<>();
        base.put("uid", u.getUid());
        base.put("email", u.getEmail());
        if (u.getDisplayName() != null && !u.getDisplayName().isEmpty()) {
            base.put("displayName", u.getDisplayName());
        }
        return users.document(u.getUid()).set(base, SetOptions.merge());
    }

    /** Read the signed-in user’s profile document (users/{uid}). */
    public Task<DocumentSnapshot> getMyProfile() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return Tasks.forException(new IllegalStateException("Not signed in"));
        return users.document(u.getUid()).get();
    }

    /** Create/merge common profile fields. Pass null for fields you don’t want to change. */
    public Task<Void> upsertMyProfile(String displayName, String phone, String address, String photoUrl) {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return Tasks.forException(new IllegalStateException("Not signed in"));

        Map<String, Object> data = new HashMap<>();
        data.put("uid", u.getUid());
        data.put("email", u.getEmail());
        if (displayName != null) data.put("displayName", displayName);
        if (phone != null)       data.put("phone", phone);
        if (address != null)     data.put("address", address);
        if (photoUrl != null)    data.put("photoUrl", photoUrl);

        return users.document(u.getUid()).set(data, SetOptions.merge());
    }

    /** Update specific fields (e.g., { "phone": "..." }). */
    public Task<Void> updateFields(Map<String, Object> updates) {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return Tasks.forException(new IllegalStateException("Not signed in"));
        return users.document(u.getUid()).update(updates);
    }

    /** Sign out helper. */
    public void signOut() {
        auth.signOut();
    }
}