package gramtarang.mint.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import gramtarang.mint.R;


/*Extra or separate class for Alrert dialog which is common for some classes*/
public class DialogActivity {
    public static class DialogCaller {
        public static void showDialog(Context context, String title, String message,
                                      DialogInterface.OnClickListener onClickListener) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context,R.style.Theme_RJDialog);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setPositiveButton("Ok",onClickListener);
            dialog.setIcon(R.drawable.erroricon);
            dialog.show();

        }
    }

}
