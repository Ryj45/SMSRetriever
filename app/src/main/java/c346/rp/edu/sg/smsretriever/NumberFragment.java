package c346.rp.edu.sg.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NumberFragment extends Fragment {

    Button btnNumber;
    EditText etNumber;
    TextView tvSMS1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_number, container, false);

        btnNumber = view.findViewById(R.id.btnNumber);
        etNumber = view.findViewById(R.id.etNumber);
        tvSMS1 = view.findViewById(R.id.tvSMS1);

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                Uri uri = Uri.parse("content://sms");
                String number = etNumber.getText().toString();

                String[] reqCols = new String[]{"type", "address", "date", "body"};

                ContentResolver cr = getActivity().getContentResolver();

                String filter = "address LIKE ?";
                String condition = "%" + number + "%";

                String[] args = {condition};
                Cursor cursor = cr.query(uri, reqCols, filter, args, null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do{
                        String type = cursor.getString(0);
                        if(type.equalsIgnoreCase("1")){
                            type = "Inbox: ";
                        }else{
                            type = "Sent: ";
                        }
                        String address = cursor.getString(1);
                        long dateInMillis = cursor.getLong(2);
                        String date = (String) DateFormat.format("dd mm yyyy h:mm:ss aa", dateInMillis);
                        String body = cursor.getString(3);
                        smsBody += type + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    }while (cursor.moveToNext());
                }
                tvSMS1.setText(smsBody);
            }
        });
        return view;
    }
}
