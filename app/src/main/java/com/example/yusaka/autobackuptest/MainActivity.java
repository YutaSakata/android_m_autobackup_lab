package com.example.yusaka.autobackuptest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

  public static final Object LOCK = new Object();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.cpA).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CopyTask().execute("a.zip");
      }
    });
    findViewById(R.id.cpB).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CopyTask().execute("b.zip");
      }
    });
    findViewById(R.id.cpC).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CopyTask().execute("c.zip");
      }
    });
    findViewById(R.id.cpD).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CopyTask().execute("d.zip");
      }
    });
    findViewById(R.id.calcSum).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CalcChecksumTask().execute();
      }
    });
    for (File f : getFilesDir().listFiles()) {
      Log.d("hoge", f.getAbsolutePath());
    }
  }

  private void copyFilesFromAssets(String fName){
    synchronized (LOCK) {
      InputStream is = null;
      FileOutputStream fos = null;
      try {
        is = getAssets().open(fName);
        fos = openFileOutput("stub_file", MODE_PRIVATE);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) >= 0) {
          fos.write(buf, 0, len);
        }
      } catch (IOException ignore) {
      } finally {
        try {
          if (is != null) {
            is.close();
          }
          if (fos != null) {
            fos.close();
          }
        } catch (IOException ignore) {
        }
      }
    }
  }

  private String calcCheckSum() {
    synchronized (LOCK) {
      FileInputStream fis = null;
      try {
        fis = openFileInput("stub_file");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buf = new byte[1024];
        int len;
        while ((len = fis.read(buf)) >= 0) {
          md.update(buf, 0, len);
        }
        return String.format("%032X", new BigInteger(1, md.digest()));
      } catch (NoSuchAlgorithmException | IOException e) {
        e.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
          }
        }
      }
    }
    return null;
  }


  private class CopyTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
      String fName = params[0];
      copyFilesFromAssets(fName);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      Toast.makeText(MainActivity.this, "Copy has been done.", Toast.LENGTH_LONG).show();
    }
  }

  private class CalcChecksumTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
      return calcCheckSum();
    }

    @Override
    protected void onPostExecute(String md5hash) {
      super.onPostExecute(md5hash);
      ((TextView)findViewById(R.id.checksum)).setText(md5hash);
    }
  }

}
