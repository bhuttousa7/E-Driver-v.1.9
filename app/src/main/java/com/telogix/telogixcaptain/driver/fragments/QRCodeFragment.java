package com.telogix.telogixcaptain.driver.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;
import com.telogix.telogixcaptain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {


    private CodeScanner codeScanner;

    public QRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_qrcode, container, false);
        CodeScannerView scannerView =  v.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(getContext(), scannerView);

        // Parameters (default values)
        codeScanner.setCamera(CodeScanner.CAMERA_BACK); // or CAMERA_FRONT or specific camera id
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);  // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE ); // or CONTINUOUS
        codeScanner.setScanMode(ScanMode.SINGLE);  // or CONTINUOUS or PREVIEW
        codeScanner.setAutoFocusEnabled(true); // Whether to enable auto focus or not
        codeScanner.setFlashEnabled(false); // Whether to enable flash or not

        // Callbacks
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @SuppressLint("WrongThread")
            @Override
            public void onDecoded(@NonNull Result result) {

                try {


getFragmentManager().popBackStack();
                    Intent i = new Intent(getContext(), DecantingListActivity.class);
                    i.putExtra("routeid", "" + -1);
                    i.putExtra("vehicleid", "" + -1);
                    i.putExtra("decantinglist",true);
                    getContext().startActivity(i);
                }catch (Exception ex)
                {

                }
            }
        });
        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), error.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        codeScanner.startPreview();
    return  v;





}
    @Override
    public void onResume() {
        super.onResume();
        if(codeScanner!=null)
        {
        codeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        if(codeScanner!=null) {
            codeScanner.releaseResources();
        }
        super.onPause();
    }

    private void ChangeFragment(Fragment fragment, Bundle b) {
        fragment.setArguments(b);

        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }
}
