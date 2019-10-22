package com.example.progressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Dialog dialog;
    ProgressBar progressBar;
    int progreso;
    TextView loadingMessage, progressMessage;
    TareaAsincrona tareaAsincrona;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.boton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tareaAsincrona = new TareaAsincrona();
                tareaAsincrona.execute(0);
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        tareaAsincrona = new TareaAsincrona();
        tareaAsincrona.execute(savedInstanceState.getInt("VALUE"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (progressBar!=null && tareaAsincrona!=null)
        {
            outState.putInt("VALUE",progressBar.getProgress());
            tareaAsincrona.cancel(true);
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (tareaAsincrona != null && tareaAsincrona.isCancelled())
        {
            tareaAsincrona = new TareaAsincrona();
            tareaAsincrona.execute(progreso);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (tareaAsincrona != null && progressBar!= null)
        {
            progreso = progressBar.getProgress();
            tareaAsincrona.cancel(true);
            dialog.dismiss();
        }
    }

    class TareaAsincrona extends AsyncTask<Integer,Integer,Boolean> {

        @Override
        protected void onPreExecute()
        {
            setDialog();
        }

        protected Boolean doInBackground(Integer... values)
        {
            try
            {
                for (int i = values[0]; i<=100; i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            progreso = values[0].intValue();

            progressBar.setProgress(progreso);
            progressMessage.setText(values[0] + "/100");
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result)
            {
                Toast.makeText(MainActivity.this, "Tarea Finalizada!!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }

        private  void setDialog()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialogo_progress,null);
            progressBar = ((View) view).findViewById(R.id.loader);
            loadingMessage = view.findViewById(R.id.loadingmsg);
            progressMessage = view.findViewById(R.id.progressmsg);
            progressBar.setMax(100);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }


    }
}
