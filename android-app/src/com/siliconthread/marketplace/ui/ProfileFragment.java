package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.siliconthread.marketplace.R;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        View signOut = root.findViewById(R.id.profile_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getActivity(), "Demo build — sign out disabled", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}
